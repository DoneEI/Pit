package core.command.internal;

import base.config.PitConfig;
import base.constants.PitConstant;
import base.enums.IdTNodeEnum;
import base.enums.PitResultEnum;
import base.exception.PitException;
import base.utils.SerializationUtils;
import base.utils.StringUtils;
import core.command.BaseCmd;
import entity.IndexTree;
import entity.Tree;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: write-tree  内部命令(把暂存区的内容转换成一个 tree object)
 * @author: RealGang
 * @create: 2021-02-01 09:47
 **/
public class WriteTreeCmd extends BaseCmd {

    // "{repName}/.pit"
    private static final String repRootPath =
            PitConfig.PIT_REPOSITORY + PitConfig.FILE_SEPARATOR + PitConstant.PIT_REPOSITORY_NAME;

    public static void run(String[] args) throws Exception {
        baseLogic(() -> {

        });
    }

    /**
     * @param rootIndex
     * @Description: 核心逻辑  根据IndexTree 生成Tree对象,得到tree的idx
     * @return: java.lang.String
     */
    public static String writeTree(IndexTree rootIndex) throws IOException {
        if (rootIndex.getChild().isEmpty()) {
            throw new PitException(PitResultEnum.ERROR, "error: cannot transform the indexTree to a tree");
        }
        HashMap<String, String> treeMap = trans2Tree(rootIndex, rootIndex.getFileName());
        return treeMap.getOrDefault("idx", null);
    }

    /**
     * @param indexNode
     * @param filePath  文件夹路径
     * @Description: 递归转换indexTree为tree
     * @return: java.util.HashMap<java.lang.String, java.lang.String>
     * map以"fileName"和"idx"作为key，存放对应的value值
     */
    private static HashMap<String, String> trans2Tree(IndexTree indexNode, String filePath) throws IOException {
        HashMap<String, String> resultMap = new HashMap<>();

        // 如果是文件，即叶子节点
        if (StringUtils.equal(indexNode.getNodeType(), IdTNodeEnum.FILE_NODE.getCode())) {
            resultMap.put("fileName", indexNode.getFileName());
            resultMap.put("idx", indexNode.getIdx());
            return resultMap;
        }

        Tree tree = new Tree();
        // idxs 用于拼接所有子节点的idx
        StringBuilder idxs = new StringBuilder();
        HashMap<String, String> map;
        // 遍历子节点
        for (Map.Entry<String, IndexTree> entry : indexNode.getChild(true).entrySet()) {
            map = trans2Tree(entry.getValue(), filePath + PitConfig.FILE_SEPARATOR + entry.getKey());
            tree.getChild(true).put(map.get("fileName"), map.get("idx"));
            idxs.append(map.get("idx"));
        }

        /*
          这里将“文件夹路径  文件夹时间戳  文件夹下的tree的所有的idx的拼接” 作为SHA1算法的对象,
          在执行 write-tree 命令后，如果相关文件更改，则对应文件的父节点以及直系祖宗节点都要重新建立索引,
          这也是设计上述SHA1算法对象的原因
        */
        String SHA1Content = filePath + indexNode.getTimeStamp() + idxs.toString();
        String newIdx = DigestUtils.sha1Hex(SHA1Content).substring(0, 40);
        String serializePath =
                repRootPath + PitConfig.FILE_SEPARATOR + "object" + PitConfig.FILE_SEPARATOR + newIdx.substring(0, 2);
        // 序列化tree对象
        SerializationUtils.serialize(tree, serializePath, newIdx.substring(2));

        resultMap.put("fileName", indexNode.getFileName());
        resultMap.put("idx", newIdx);
        return resultMap;
    }
}
