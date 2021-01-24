package base.utils;

import base.config.PitConfig;
import base.exception.PitException;


import java.io.*;

/**
 * 序列化工具
 * @Author: DoneEI
 * @Since: 2021/1/21 3:10 下午
 **/
public class SerializationUtil {

    /**
     * @Description: 将传入的对象序列化后通过IO流写入到对应hash值构成的文件路径下
     * @Param: Idx(对应hash值)
     * @Param: Object(要序列化的对象)
     * @return:
     * @Author: RealGang
     * @Date: 2021/1/24
     */
    public static void serialize(String idx,Object object){
        String prefixPath = "";
        String suffixPath = "";
        prefixPath = idx.substring(0,2);
        suffixPath = idx.substring(2);

        // 获取系统文件分隔符
        String separator = System.getProperty("file.separator");
        File objectsDir = new File(PitConfig.PIT_REPOSITORY+separator+"objects");
        File prefixDir = new File(PitConfig.PIT_REPOSITORY+separator+"objects"+separator+prefixPath);
        if(!objectsDir.exists()){
            objectsDir.mkdir();
        }
        if(!prefixDir.exists()){
            prefixDir.mkdir();
        }

        String fileName=suffixPath;
        try {
            File objFile=new File(prefixDir+separator+fileName);
            FileOutputStream fileOutputStream=null;
            if(!objFile.exists()){
                objFile.createNewFile();
            }
            fileOutputStream = new FileOutputStream(objFile);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            // 序列化 对象
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
        } catch (PitException pitEx) {
            OutputUtil.output(pitEx.getErrorMsg());
        } catch (IOException e) {
            OutputUtil.output("Pit System Error! See the exception message below:");
            e.printStackTrace();
        }
    }

    /**
     * @Description:通过idx获取到对应读取文件的路径，然后通过IO流读取到文件内容反序列化得到对象
     * @param idx
     * @return: T
     * @Author: RealGang
     * @Date: 2021/1/24
     */
    public static <T> T deserialize(String idx){
        String prefixPath = "";
        String suffixPath = "";
        prefixPath = idx.substring(0,2);
        suffixPath = idx.substring(2);

        // 获取系统文件分隔符
        String separator = System.getProperty("file.separator");
        File file = new File(PitConfig.PIT_REPOSITORY+separator+"objects"+separator+prefixPath+separator+suffixPath);
        if(!file.exists()){
            return null;
        }
        try {
            FileInputStream inputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            return (T)objectInputStream.readObject();
        } catch (PitException pitEx) {
            OutputUtil.output(pitEx.getErrorMsg());
        } catch (IOException e) {
            OutputUtil.output("Pit System Error! See the exception message below:");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
