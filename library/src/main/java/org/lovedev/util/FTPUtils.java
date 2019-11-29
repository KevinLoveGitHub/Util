package org.lovedev.util;

import android.os.Environment;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketException;
import java.text.ParseException;

/**
 * ftp工具
 *
 * @author Kevin
 * @date 2017/7/19
 */
public class FTPUtils {

    private static final String TAG = "FTPUtils";
    private FTPClient ftpClient = null;
    private static FTPUtils instance = null;
    private String FTPUrl;
    private int FTPPort = 21;
    private String UserName;
    private String UserPassword;
    private String workingDirectory = "log";


    /**
     * 得到类对象实例（因为只能有一个这样的类对象，所以用单例模式）
     */
    private static class FTPUtilsHolder {
        private static final FTPUtils INSTANCE = new FTPUtils();
    }

    public static FTPUtils getInstance() {
        return FTPUtilsHolder.INSTANCE;
    }

    private FTPUtils() {
    }

    /**
     * 设置FTP服务器
     *
     * @param workingDirectory 工作空间
     * @param ftpUrl           FTP服务器ip地址
     * @param ftpPort          FTP服务器端口号
     * @param userName         登陆FTP服务器的账号
     * @param userPassword     登陆FTP服务器的密码
     * @return 是否设置成功
     */
    public boolean initFTPSetting(String workingDirectory, String ftpUrl, int ftpPort, String userName, String userPassword) {

        this.workingDirectory = workingDirectory;
        this.FTPUrl = ftpUrl;
        this.FTPPort = ftpPort;
        this.UserName = userName;
        this.UserPassword = userPassword;
        ftpClient = new FTPClient();

        int reply;

        try {
            //1.要连接的FTP服务器Url,Port
            ftpClient.connect(ftpUrl, ftpPort);

            //2.登陆FTP服务器
            ftpClient.login(userName, userPassword);

            //3.看返回的值是不是230，如果是，表示登陆成功
            reply = ftpClient.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                //断开
                ftpClient.disconnect();
                return false;
            }

            return true;

        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 上传文件
     *
     * @param filePath 要上传文件所在SDCard的路径
     * @param fileName 要上传的文件的文件名(如：Sim唯一标识码)
     * @return true为成功，false为失败
     */
    public boolean uploadFile(String filePath, String fileName) {
        if (ftpClient == null) {
            throw new NullPointerException("please call initFTPSetting() first");
        }
        if (!ftpClient.isConnected()) {
            if (!initFTPSetting(workingDirectory, FTPUrl, FTPPort, UserName, UserPassword)) {
                return false;
            }
        }

        try {

            //设置存储路径
            ftpClient.makeDirectory(workingDirectory);
            ftpClient.changeWorkingDirectory(workingDirectory);

            //设置上传文件需要的一些基本信息
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            //文件上传吧～
            FileInputStream fileInputStream = new FileInputStream(filePath);
            ftpClient.storeFile(fileName, fileInputStream);

            //关闭文件流
            fileInputStream.close();

            //退出登陆FTP，关闭ftpCLient的连接
            ftpClient.logout();
            ftpClient.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 上传文件
     *
     * @param pathname       ftp服务保存地址
     * @param fileName       上传到ftp的文件名
     * @param originFileName 待上传文件的名称（绝对地址） *
     * @return 是否成功
     */
    public boolean uploadFile(String pathname, String fileName, String originFileName) {

        if (ftpClient == null) {
            throw new NullPointerException("please call initFTPSetting() first");
        }
        if (!ftpClient.isConnected()) {
            if (!initFTPSetting(pathname, FTPUrl, FTPPort, UserName, UserPassword)) {
                return false;
            }
        }

        FileInputStream fileInputStream = null;
        try {

            //设置存储路径
            ftpClient.makeDirectory(pathname);
            ftpClient.changeWorkingDirectory(pathname);

            //设置上传文件需要的一些基本信息
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            //文件上传吧～
            fileInputStream = new FileInputStream(originFileName);
            ftpClient.storeFile(fileName, fileInputStream);

            //关闭文件流
            fileInputStream.close();


        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            //退出登陆FTP，关闭ftpCLient的连接
            try {
                ftpClient.logout();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != fileInputStream) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * 获取文件最后修改时间
     *
     * @param filePath 要存放的文件的路径
     * @param fileName 远程FTP服务器上的那个文件的名字
     * @return 最后修改时间的时间戳
     */
    public Long getFileLastModifiedTime(String filePath, String fileName) throws IOException {
        if (ftpClient == null) {
            throw new NullPointerException("please call initFTPSetting() first");
        }
        if (!ftpClient.isConnected()) {
            if (!initFTPSetting(filePath, FTPUrl, FTPPort, UserName, UserPassword)) {
                return null;
            }
        }

        boolean result = false;

        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.changeWorkingDirectory(filePath);

        // 列出该目录下所有文件
        FTPFile[] files = ftpClient.listFiles();

        // 遍历所有文件，找到指定的文件
        for (FTPFile file : files) {
            if (file.getName().equals(fileName)) {
                return file.getTimestamp().getTimeInMillis();
            }
        }

        //退出登陆FTP，关闭ftpCLient的连接
        ftpClient.logout();
        ftpClient.disconnect();
        return null;
    }

    /**
     * 获取文件最后修改时间
     *
     * @param filePath 要存放的文件的路径
     * @param fileName 远程FTP服务器上的那个文件的名字
     * @return 文件大小
     */
    public Long getFileSize(String filePath, String fileName) throws IOException {
        if (ftpClient == null) {
            throw new NullPointerException("please call initFTPSetting() first");
        }
        if (!ftpClient.isConnected()) {
            if (!initFTPSetting(filePath, FTPUrl, FTPPort, UserName, UserPassword)) {
                return null;
            }
        }

        boolean result = false;

        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.changeWorkingDirectory(filePath);

        // 列出该目录下所有文件
        FTPFile[] files = ftpClient.listFiles();

        // 遍历所有文件，找到指定的文件
        for (FTPFile file : files) {
            if (file.getName().equals(fileName)) {
                return file.getSize();
            }
        }

        //退出登陆FTP，关闭ftpCLient的连接
        ftpClient.logout();
        ftpClient.disconnect();
        return null;
    }

    /**
     * 下载文件
     *
     * @param filePath 要存放的文件的路径
     * @param fileName 远程FTP服务器上的那个文件的名字
     * @return true为成功，false为失败
     */
    public boolean downLoadFile(String filePath, String fileName) throws IOException {
        if (ftpClient == null) {
            throw new NullPointerException("please call initFTPSetting() first");
        }
        if (!ftpClient.isConnected()) {
            if (!initFTPSetting(workingDirectory, FTPUrl, FTPPort, UserName, UserPassword)) {
                return false;
            }
        }

        boolean result = false;

        // 转到指定下载目录
        String sdcardPath = Environment.getExternalStorageDirectory().getPath();

        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.changeWorkingDirectory(workingDirectory);

        // 列出该目录下所有文件
        FTPFile[] files = ftpClient.listFiles();

        // 遍历所有文件，找到指定的文件
        for (FTPFile file : files) {
            if (file.getName().equals(fileName)) {
                //根据绝对路径初始化文件
                File localFile = new File(filePath + "/" + fileName);

                // 输出流
                OutputStream outputStream = new FileOutputStream(localFile);

                // 下载文件
                ftpClient.retrieveFile(file.getName(), outputStream);

                //关闭流
                outputStream.close();

                result = true;
            }
        }

        //退出登陆FTP，关闭ftpCLient的连接
        ftpClient.logout();
        ftpClient.disconnect();
        return result;
    }


    public boolean deleteFile(String ftpFilePath, String fileName) {
        if (ftpClient == null) {
            throw new NullPointerException("please call initFTPSetting() first");
        }
        if (!ftpClient.isConnected()) {
            if (!initFTPSetting(ftpFilePath, FTPUrl, FTPPort, UserName, UserPassword)) {
                return false;
            }
        }

        boolean result = false;

        try {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.changeWorkingDirectory(ftpFilePath);
            result = ftpClient.deleteFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //退出登陆FTP，关闭ftpCLient的连接
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return result;
    }


    /**
     * 下载文件
     *
     * @param workPath FTP工作路径
     * @param filePath 要存放的文件的路径
     * @param fileName 远程FTP服务器上的那个文件的名字
     * @return true为成功，false为失败
     */
    public boolean downLoadFile(String workPath, String filePath, String fileName) throws IOException {
        if (ftpClient == null) {
            throw new NullPointerException("please call initFTPSetting() first");
        }
        if (!ftpClient.isConnected()) {
            if (!initFTPSetting(workPath, FTPUrl, FTPPort, UserName, UserPassword)) {
                return false;
            }
        }

        boolean result = false;

        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.changeWorkingDirectory(workPath);

        // 列出该目录下所有文件
        FTPFile[] files = ftpClient.listFiles();

        // 遍历所有文件，找到指定的文件
        for (FTPFile file : files) {
            if (file.getName().equals(fileName)) {
                //根据绝对路径初始化文件
                File localFile = new File(filePath + "/" + fileName);

                // 输出流
                OutputStream outputStream = new FileOutputStream(localFile);

                // 下载文件
                ftpClient.retrieveFile(file.getName(), outputStream);

                //关闭流
                outputStream.close();

                result = true;
            }
        }

        //退出登陆FTP，关闭ftpCLient的连接
        ftpClient.logout();
        ftpClient.disconnect();
        return result;
    }


    /**
     * @param fileName
     * @return 从服务器读取文件内容
     * @throws ParseException 解析异常
     * @throws IOException    IO 操作异常
     */
    public String readFile(String fileName) throws ParseException, IOException {
        if (ftpClient == null) {
            throw new NullPointerException("please call initFTPSetting() first");
        }
        String code = "GBK";
        InputStream ins = null;
        StringBuilder builder = null;
        ftpClient.changeWorkingDirectory(workingDirectory);
        // 從服務器上讀取指定的文件
        ins = ftpClient.retrieveFileStream(fileName);
        if (ins == null) {
            throw new NullPointerException("找不到文件");
        }
        InputStreamReader inputStreamReader = new InputStreamReader(ins, code);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line;
        builder = new StringBuilder(150);
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        ins.close();
        //退出登陆FTP，关闭ftpCLient的连接
        ftpClient.logout();
        ftpClient.disconnect();
        return String.valueOf(builder);
    }

    /**
     * @param fileName
     * @return 从服务器读取文件内容
     * @throws ParseException 解析异常
     * @throws IOException    IO 操作异常
     */
    public String readFile(String path, String fileName) throws ParseException, IOException, NullPointerException {
        if (ftpClient == null) {
            throw new NullPointerException("please call initFTPSetting() first");
        }
        if (!ftpClient.isConnected()) {
            if (!initFTPSetting(path, FTPUrl, FTPPort, UserName, UserPassword)) {
                return null;
            }
        }
        String code = "GBK";
        InputStream ins = null;
        StringBuilder builder = null;
        ftpClient.changeWorkingDirectory(path);
        // 從服務器上讀取指定的文件
        ins = ftpClient.retrieveFileStream(fileName);
        if (ins == null) {
            throw new NullPointerException("找不到文件");
        }
        InputStreamReader inputStreamReader = new InputStreamReader(ins, code);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line;
        builder = new StringBuilder(150);
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        ins.close();
        //退出登陆FTP，关闭ftpCLient的连接
        ftpClient.logout();
        ftpClient.disconnect();
        return String.valueOf(builder);
    }


    /**
     * 判断ftp服务器文件是否存在
     *
     * @param path     FTP服务器路径
     * @param fileName FTP文件名
     * @return 是否存在
     */
    public boolean checkFileExist(String path, String fileName) {

        boolean flag = false;
        if (ftpClient == null) {
            throw new NullPointerException("please call initFTPSetting() first");
        }
        if (!ftpClient.isConnected()) {
            if (!initFTPSetting(path, FTPUrl, FTPPort, UserName, UserPassword)) {
                return flag;
            }
        }
        String code = "GBK";
        InputStream ins = null;
        StringBuilder builder = null;
        try {
            ftpClient.changeWorkingDirectory(path);
            // 從服務器上讀取指定的文件
            ins = ftpClient.retrieveFileStream(fileName);
            flag = ins != null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return flag;
    }

    //改变目录路径
    public boolean changeWorkingDirectory(String directory) {
        boolean flag = true;
        try {
            flag = ftpClient.changeWorkingDirectory(directory);
            if (flag) {
                Log.i(TAG, "进入文件夹" + directory + " 成功！");

            } else {
                Log.i(TAG, "进入文件夹" + directory + " 失败！开始创建文件夹");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return flag;
    }

    //创建多层目录文件，如果有ftp服务器已存在该文件，则不创建，如果无，则创建
    public boolean createDirecroty(String remote) throws IOException {
        String directory = remote + "/";
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!directory.equalsIgnoreCase("/") && !changeWorkingDirectory(directory)) {
            int start = 0;
            int end = 0;
            if (directory.startsWith("/")) {
                start = 1;
            }
            end = directory.indexOf("/", start);
            String path = "";
            StringBuilder paths = new StringBuilder();
            while (true) {
                String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), "iso-8859-1");
                path = path + "/" + subDirectory;
                if (!existFile(path)) {
                    if (makeDirectory(subDirectory)) {
                        changeWorkingDirectory(subDirectory);
                    } else {
                        Log.i(TAG, "创建目录[" + subDirectory + "]失败");
                        changeWorkingDirectory(subDirectory);
                    }
                } else {
                    changeWorkingDirectory(subDirectory);
                }
                paths.append("/").append(subDirectory);
                start = end + 1;
                end = directory.indexOf("/", start);
                // 检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        }
        return true;
    }

    //判断ftp服务器文件是否存在
    public boolean existFile(String path) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        if (ftpFileArr.length > 0) {
            flag = true;
        }
        return flag;
    }

    //创建目录
    public boolean makeDirectory(String dir) {
        boolean flag = true;
        try {
            flag = ftpClient.makeDirectory(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

}
