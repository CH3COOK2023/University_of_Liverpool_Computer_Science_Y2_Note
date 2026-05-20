package Tools;

import Constant.BaseException;
import Constant.BaseExceptionType;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GraphMaker {
    private BufferedWriter br;
    public GraphMaker(String filePath){
        try {
            br = new BufferedWriter(new FileWriter(filePath,true));
        } catch (IOException ignored) {
            throw new BaseException(BaseExceptionType.FILE_IO_EXCEPTION);
        }
    }

    public void record(int x, int y1, int y2){
        try{
            br.write(String.format("%d\t%d\t%d",x,y1,y2));
            br.newLine();
            br.flush();
        }catch (Exception ignored){
            throw new BaseException(BaseExceptionType.FILE_IO_EXCEPTION);
        }
    }


}
