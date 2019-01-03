package ss.week6.dictionaryattack;
import java.io.*;
import java.util.*;

import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DictionaryAttack {  
	private Map<String, String> passwordMap;
	private Map<String, String> hashDictionary;

	/**P 6.18
	 * Reads a password file. Each line of the password file has the form:
	 * username: encodedpassword
	 * After calling this method, 
	 * the passwordMap class variable should be filled with the content of the file. 
	 * @param filename 
	 */
	public void readPasswords(String filename) 
			throws FileNotFoundException, IOException 
	{
		String username;
		String hashedpassword;
		
		BufferedReader br = null;
		
		try {
		
			String currentLine;
			br = new BufferedReader(new FileReader(filename));
			while ((currentLine = br.readLine()) != null) 
			{
				if (currentLine.contains(": "))
				{
					String[] parts =currentLine.split(": ");
					username=parts[0];
					hashedpassword=parts[1];
					passwordMap.put(username, hashedpassword);
					//the key for the map should be the username
					//the password hash should be the content.
				}
			}
		} catch (FileNotFoundException e){
			System.out.println(e.getMessage());
		}catch (IOException e){
			System.out.println(e.getMessage());
		}finally{
			br.close();//has to close
		}
		
		/**question if (a condition), throws new FileNotFoundException
		or catch (FileNotFoundException e){ handle the exception} ?*/
	}

	/**P6.19
	 * input: String password
	 * step1: password-> MD5 hash of this password (a byte array)
	 * step2: MD5 hash of this password -> Hex-encoded string
	 * step3: return Hex-encoded string 
	 * return the MD5 hash of a password. 
	 * The resulting hash (or sometimes called digest) should be hex-encoded in a String.
	 * @param password
	 * @return
	 */
	/**answer to the question: which users appear to have chosen ¡°5f4dcc3b5aa765d61d8327deb882cf99¡± as their password?
	 * answer: bob,dave,debra,diane,fred,raj */
	public String getPasswordHash(String password) throws NoSuchAlgorithmException 

	{
		String result=null;
		try{
		    MessageDigest md = MessageDigest.getInstance("MD5");
		    byte[] md5hash = md.digest(password.getBytes());
		    String hexEncodedOfmd5=Hex.encodeHexString(md5hash);
	    	result= hexEncodedOfmd5;
		}catch (NoSuchAlgorithmException e){
			System.out.println(e.getMessage());
			//("MD5 algorithm does not exist");
			
		}
		return result;
	}
	

	
	/**
	 * Checks the password for the user the password list. 
	 * If the user does not exist, returns false.
	 * @param user
	 * @param password
	 * @return whether the password for that user was correct.
	 * @throws NoSuchAlgorithmException 
	 */
	public boolean checkPassword(String user, String password) throws NoSuchAlgorithmException {
		String passwordHash;
		try{
			passwordHash=getPasswordHash(password);
		}catch (NoSuchAlgorithmException e){
			System.out.println("NoSuchAlgorithmException");
			throw e;
		}
    	if (!(passwordMap.isEmpty()))
    	{
    		for (String usernameInMap : passwordMap.keySet()) 
        	{
        		if ((usernameInMap.equals(user)) && (passwordMap.get(usernameInMap).equals(passwordHash)) )
        		{
        				return true; 
        		}
        	}
    	}	
		return false;
	}

	/**P 6.21
	 * it should read a file 
	 * compute the (MD5) hash of each line (use your getPasswordHash)
	 * fill a dictionary (a Map) that can map a password hash back to the original password. 
	 * Search on the internet for ¡°most common passwords¡± and you will find for example a list of the
25 most common passwords. Use this list to populate a small initial dictionary to test with
	 * 
	 * 
	 * Reads a dictionary from file (one line per word) and use it to add
	 * entries to a dictionary that maps password hashes (hex-encoded) to the original password.
	 * @param filename filename of the dictionary.
	 * @throws FileNotFoundException 
	 */
    	public void addToHashDictionary(String filename) 
    			throws FileNotFoundException, IOException, NoSuchAlgorithmException	
    	{
			String originalPassword;
			String hexedHasedPassword;
			BufferedReader br = new BufferedReader(new FileReader(filename));
			while ((originalPassword = br.readLine()) != null) 
			{
				hexedHasedPassword=getPasswordHash(originalPassword);
				hashDictionary.put(hexedHasedPassword, originalPassword);
			}
			br.close();
    	}
    	
	/**
	 * Do the dictionary attack.
	 */
	public void doDictionaryAttack() 
	{
		Map<String,String> matchedMap= new HashMap<String, String>();
		for ( String username: passwordMap.keySet()) 
    	{
    		for (String hasedPw: hashDictionary.keySet())
    		{
    			if (passwordMap.get(username).equals(hasedPw) )
    			{
    				matchedMap.put(username, hashDictionary.get(hasedPw));
    			}
    		}
    	}
		for (String username: matchedMap.keySet())
		{
			System.out.println(username+": "+ matchedMap.get(username));
		}
	}
	public static void main(String[] args) throws FileNotFoundException, IOException, NoSuchAlgorithmException {
		DictionaryAttack dictionaryAttack= new DictionaryAttack();
		dictionaryAttack.readPasswords("d:\\PasswordMap.txt");
		dictionaryAttack.addToHashDictionary("d:\\OriginalPassword.txt");
		dictionaryAttack.doDictionaryAttack();
	}
}
