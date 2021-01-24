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
     * @Description: 将对象序列化，并且写入到对应对象的路径下
     * @Param: object
     * @return: byte
     * @Author: RealGang
     * @Date: 2021/1/22
     */
    public static byte[] serialize(Object object){
        //通过该对象获取该对象的内容等信息，然后调用SHA-1校验和计算出对应的40位字符串，得到对应的保存路径

        //序列化流 （输出流） --> 表示向一个目标 写入数据
        ObjectOutputStream objectOutputStream = null;
        //字节数组输出流
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            //创建一个缓冲区
            byteArrayOutputStream = new ByteArrayOutputStream();
            //将 对象 序列化成 字节后  输入缓冲区中
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            //序列化 对象
            objectOutputStream.writeObject(object);
            //得 到 序列化字节
            byte[] bytes = byteArrayOutputStream.toByteArray();

            //清空输出流
            objectOutputStream.flush();
            //释放资源
            objectOutputStream.close();

            return bytes;
        } catch (PitException pitEx) {
            OutputUtil.output(pitEx.getErrorMsg());
        } catch (Exception e){
            OutputUtil.output("Pit System Error! See the exception message below:");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Description: 反序列化
     * @Param: 
     * @return: 
     * @Author: RealGang
     * @Date: 2021/1/22
     */
    public static <T> T deserialize(byte[] bytes,Class<T> clazz){
        //字节数组
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            //将 得到的序列化字节 丢进 缓冲区
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            //反序列化流 （输入流）--> 表示着从 一个 源头 读取 数据 ， （读取 缓冲区中 的字节）
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            //反序化成 一个对象
            return (T)objectInputStream.readObject();

        } catch (PitException pitEx) {
            OutputUtil.output(pitEx.getErrorMsg());
        } catch (Exception e){
            OutputUtil.output("Pit System Error! See the exception message below:");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Description: 将序列化后的数据写入到对应hash值构成的文件路径下
     * @Param: Idx(对应hash值)
     * @Param: serializeResult(序列化结果)
     * @return:
     * @Author: RealGang
     * @Date: 2021/1/24
     */
    public static void writeObject(String idx,byte[] serializeResult){
        String prefixPath = "";
        String suffixPath = "";
        prefixPath = idx.substring(0,2);
        suffixPath = idx.substring(2);

        // 获取系统文件分隔符
        String separator = System.getProperty("file.separator");
//        String PIT_REPOSITORY="G:\\lab\\Git手写\\projectTest\\.pit";
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
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
            outputStreamWriter.write(String.valueOf(serializeResult));
            outputStreamWriter.close();
        } catch (PitException pitEx) {
            OutputUtil.output(pitEx.getErrorMsg());
        } catch (IOException e) {
            OutputUtil.output("Pit System Error! See the exception message below:");
            e.printStackTrace();
        }

    }

    /**
     * @Description:
     * @param idx
     * @return: byte[]
     * @Author: RealGang
     * @Date: 2021/1/24
     */
    public static byte[] readObject(String idx){
        String prefixPath = "";
        String suffixPath = "";
        prefixPath = idx.substring(0,2);
        suffixPath = idx.substring(2);

        // 获取系统文件分隔符
        String separator = System.getProperty("file.separator");
//        String PIT_REPOSITORY="G:\\lab\\Git手写\\projectTest\\.pit";
        File file = new File(PitConfig.PIT_REPOSITORY+separator+"objects"+separator+prefixPath+separator+suffixPath);
        if(!file.exists()){
            return null;
        }
        try {
            FileInputStream inputStream = new FileInputStream(file);
            int length = inputStream.available();
            byte[]  bytes= new byte[length];
            inputStream.read(bytes);
            inputStream.close();
            return bytes;
        } catch (PitException pitEx) {
            OutputUtil.output(pitEx.getErrorMsg());
        } catch (IOException e) {
            OutputUtil.output("Pit System Error! See the exception message below:");
            e.printStackTrace();
        }
        return null;
    }
}
