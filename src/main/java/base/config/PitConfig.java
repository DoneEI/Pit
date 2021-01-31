package base.config;

import entity.IndexTree;

/**
 * 项目相关配置
 * 
 * @Author: DoneEI
 * @Since: 2021/1/21 3:04 下午
 **/
public class PitConfig {
    /**
     * 当前工作目录
     */
    public static String CURRENT_WORKING_DIRECTORY;

    /**
     * Pit目录
     */
    public static String PIT_REPOSITORY;

    /**
     * 当前系统文件分隔符
     */
    public static String FILE_SEPARATOR;

    /**
     * 暂存区根节点
     */
    public static IndexTree INDEX_TREE_ROOT_NODE;
}
