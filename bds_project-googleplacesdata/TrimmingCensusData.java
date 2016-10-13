package com.census.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author sarthak jain
 *
 */

// File for trimming the census data
public class TrimmingCensusData {

	public static void main(String args[]) throws IOException {
		/*
		 * Field numbers to include:
		 * 0,1,2,3,(PUMA00)-4,5,6,7,8,(NP)-11,12,13,15,16,17,18,(CONP)-19,28,30,32,34,38,
		 */
		BufferedReader reader = new BufferedReader(
				new FileReader(
						new File(
								"ss14hny.csv")));

		FileWriter writer = new FileWriter(new File("CensusDataTrimmedFinal.csv"));
		
		
		String line = "";
		int count=0;
		while ((line = reader.readLine()) != null) {
			count++;
			System.out.println(count);
			String[] listOfFields = line.split(",");
			for (int i=0;i<80;i++) {
				//System.out.print(listOfFields[i]+",");
				writer.write(listOfFields[i]+",");
			}
			//System.out.print(listOfFields[80]);
			writer.write(listOfFields[80]);
			writer.write("\n");
			//System.out.pr
			ntln();
		}
	writer.flush();
	writer.close();
	}
}
