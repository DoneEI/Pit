package core.command.internal;

import base.config.PitConfig;
import base.constants.PitConstant;
import base.enums.IdTNodeEnum;
import base.enums.PitResultEnum;
import base.exception.PitException;
import base.utils.FileUtils;
import base.utils.SerializationUtils;
import base.utils.StringUtils;
import entity.IndexTree;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * update index命令
 * 
 * @Author: DoneEI
 * @Since: 2021/1/21 3:14 下午
 **/
public class UpdateIndexCmd {
    private static IndexTree root;

    private static HashMap<String, IndexTree> recent;

    static {
        init();
    }

    /**
     * 初始化
     */
    private static void init() {
        try {
            root = SerializationUtils.deserialize(PitConfig.PIT_REPOSITORY + PitConfig.FILE_SEPARATOR + ".pit/index");

            if (root == null) {
                // 如果indexTree为空,创建根节点

                root = new IndexTree();

                root.setNodeType(IdTNodeEnum.DIRECTORY_NODE.getCode());
                root.setFileName("");
            }

            recent = new HashMap<>(2);
        } catch (Exception e) {
            throw new PitException(PitResultEnum.ERROR,
                "error: system error occurs when initializing index tree. See the error message below:"
                    + Arrays.toString(e.getStackTrace()));
        }

    }

    /**
     * 将文件在暂存区中更新
     * 
     * @param file
     *            待更新文件
     * @return 是否在暂存区中更新
     * @throws IOException
     *             ioe
     */
    public static boolean updateIndex(File file) throws IOException {
        String path = FileUtils.getCanonicalRelativePath(file, PitConfig.PIT_REPOSITORY);
        String fileName = file.getName();
        String parentPath = path.substring(0, path.length() - fileName.length() - PitConfig.FILE_SEPARATOR.length());

        IndexTree parent = searchParent(parentPath);
        IndexTree curFileNode;

        // 说明父节点中无该文件节点
        if (parent.getChild(true).getOrDefault(fileName, null) == null) {
            // 创建节点

            curFileNode = new IndexTree();

            curFileNode.setFileName(fileName);
            curFileNode.setNodeType(IdTNodeEnum.FILE_NODE.getCode());
            curFileNode.setIdx(HashObjectCmd.hashObject(file, "-w"));
            curFileNode.setTimeStamp(file.lastModified());

        } else {
            curFileNode = parent.getChild().get(fileName);

            if (curFileNode.getTimeStamp() != null && curFileNode.getTimeStamp() == file.lastModified()) {
                // 时间戳没变化, 不修改
                return false;
            }

            // 判断当前file的idx值
            String curIdx = HashObjectCmd.hashObject(file);

            if (!StringUtils.equal(curIdx, curFileNode.getIdx())) {
                HashObjectCmd.hashObject(file, "-w");
            }
        }

        // 更新该节点
        parent.getChild().put(fileName, curFileNode);

        return true;
    }

    /**
     * 保存暂存区
     * 
     * @throws IOException
     *             ioe
     */
    public static void updateRoot() throws IOException {
        SerializationUtils.serialize(root, PitConstant.PIT_REPOSITORY_NAME, "index");
    }

    /**
     * 在暂存区树中搜索父路径对应的节点
     * 
     * @param parentPath
     *            父路径
     * @return IndexTree 节点
     */
    private static IndexTree searchParent(String parentPath) {
        IndexTree parent;

        // 从recent查找
        if (recent.containsKey(parentPath)) {
            parent = recent.get(parentPath);
        } else {
            // 否则从根节点中寻找
            String[] pathSegment = parentPath.split(PitConfig.FILE_SEPARATOR);

            parent = root;

            // 先根据路径创建文件夹节点
            for (String s : pathSegment) {
                IndexTree cur;

                cur = parent.getChild(true).getOrDefault(s, null);

                if (cur == null) {
                    // 新建节点
                    cur = new IndexTree();
                    cur.setFileName(s);
                    cur.setNodeType(IdTNodeEnum.DIRECTORY_NODE.getCode());

                    parent.getChild().put(s, cur);
                }

                parent = cur;
            }

            recent.put(parentPath, parent);
        }

        return parent;
    }

}
