package com.simplicite.commons.Inhecryptance;

import java.util.*;

import com.simplicite.util.*;
import com.simplicite.util.exceptions.*;
import com.simplicite.util.tools.*;


public class EncryptedObjectDB extends ObjectDB {
	private static final long serialVersionUID = 1L;

	// based on https://docs.simplicite.io/documentation/01-core/advanced-code-examples.md#encryption
	@Override
	public String preSave(){
		try{
			for(ObjectField f : getFields())
				if(isEncrypted(f))
					f.setValue(EncryptionTool.encrypt(f.getValue(), getEncryptionKey()));
		}
		catch(EncryptionException e){
			AppLog.error(e, getGrant());	
		}
		
		return super.preSave();
	}
	
	@Override
	public void postSelect(String rowId, boolean copy){
		try{
			for(ObjectField f : getFields())
				if(isEncrypted(f))
					f.setValue(EncryptionTool.decrypt(f.getValue(), getEncryptionKey()));
		}
		catch(EncryptionException e){
			AppLog.error(e, getGrant());	
		}
		
		super.postSelect(rowId, copy);
	}
	
	private static boolean isEncrypted(ObjectField f){
		return !f.isEmpty() && "CONFIDENTIAL".equals(f.getClassification());
	}
	
	private static String getEncryptionKey(){
		// for ease of use, this method uses a db-stored param. It's advisable to use a JVM or System paremeter (see doc)
		return Grant.getSystemAdmin().getParameter("MY_ENCRYPTION_KEY"); // Simplicite012345
	}
}
