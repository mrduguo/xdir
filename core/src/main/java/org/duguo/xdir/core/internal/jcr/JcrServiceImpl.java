package org.duguo.xdir.core.internal.jcr;


import java.io.File;
import java.io.FileOutputStream;


import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.util.datetime.DateTimeUtil;

public class JcrServiceImpl extends AbstractUpdateJcrService implements JcrService{
	
	public void exportToFile(ModelImpl model,String nodePath,String targetFolder) throws Exception{
		File targetFile=new File(targetFolder);
		if(!targetFile.exists()){
			targetFile.mkdirs();
		}
		targetFile=new File(targetFile,nodePath.substring(nodePath.lastIndexOf("/")+1)+"-"+DateTimeUtil.currentTimestampKey()+".xml");
		FileOutputStream fileOutputStream=new FileOutputStream(targetFile);
		try{
			model.getSession().exportSystemView(nodePath, fileOutputStream, false, false);
		}finally{
			try{
				fileOutputStream.close();
			}catch(Exception ignore){}
		}
	}

}
