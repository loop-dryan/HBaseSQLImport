package net.autoloop;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.sql.Types;

public class HBaseHelper {

	/**
	 * Convert a row from the schema table to a description object.
	 */
	public static HBaseDescription getDescriptionFromResult(Result r) {

		HBaseDescription d = new HBaseDescription();
		HBaseColumn c = new HBaseColumn();
		d.setHbaseColumn(c);
		
		/*
		 *  
			Companies hbt company
			Companies k CompanyId
			Companies q [SQL Query not shown, use -query to see it]
			Companies qn Companies
			* 
			* or
			* 
			Companies_CompanyName c CompanyName
			Companies_CompanyName t 12
			Companies_CompanyName hbcf d
			Companies_CompanyName hbd The name of the company
			Companies_CompanyName hbl CompanyName
			Companies_CompanyName hbq cn
			Companies_CompanyName hbt company
			Companies_CompanyName qn Companies
		 */
		
		String rowKey = new String(r.getRow());
		
		for (KeyValue kv:r.raw()) {
			String qualifier = new String(kv.getQualifier());
			String value = new String(kv.getValue());
			switch (qualifier) {
				case "qn": // Query Name
					d.setQueryName(value);
					break;
				case "q": // The SQL query
					d.setQuery(value);
					break;
				case "k":
					d.setSqlKey(value);
					break;
				case "hbt":
					d.setTableName(value);
					break;
				case "hbcf":
					c.setColumnFamily(value);
					break;
				case "hbq":
					c.setQualifier(value);
					break;
				case "c":
					c.setSqlColumnName(value);
					break;
				case "t":
					c.setDataType(value);
					break;
				case "hbl":
					c.setLogicalName(value);
					break;
				case "hbd":
					c.setDescription(value);
					break;
				case "ty":
					d.setType(value);
					break;
				case "hbn":
					if ("true".equals(value)) {
						c.setIsNested(true);
					} else {
						c.setIsNested(false);
					}
					break;
				default: break;
			}
		}
		
		return d;
	}

	/**
	 * Convert a JSON facade object to a description object.
	 */
	public static HBaseDescription 
		getDescriptionFromJsonSchema(HBaseJsonSchema j) {

		HBaseDescription d = new HBaseDescription();
		HBaseColumn c = new HBaseColumn();
		d.setHbaseColumn(c);

		d.setQueryName(j.qn);
		d.setQuery(j.q);
		d.setSqlKey(j.k);
		d.setTableName(j.hbt);
		d.setType(j.ty);

		c.setColumnFamily(j.hbcf);
		c.setQualifier(j.hbq);
		c.setSqlColumnName(j.c);
		c.setDataType(j.t);
		c.setLogicalName(j.hbl);
		c.setDescription(j.hbd);
		if ("true".equals(j.hbn)) {
			c.setIsNested(true);
		} else {
			c.setIsNested(false);
		}
		
		return d;	
	}

	/**
	 * Get the java sql type that corresponds to the HBase column type name.
	 */
	public static int getJavaSqlDataType(String dataType) {
		switch (dataType) {
			case "int": return java.sql.Types.INTEGER; // int
			case "long": return java.sql.Types.BIGINT; // long
			case "string": return java.sql.Types.VARCHAR; // String
			case "nstring": return java.sql.Types.NVARCHAR; // String
			case "double": return java.sql.Types.DOUBLE; // double
			case "float": return java.sql.Types.FLOAT; // float
			case "boolean": return java.sql.Types.BIT; // boolean
			case "byte": return java.sql.Types.TINYINT; // byte
			case "datetime": return java.sql.Types.TIMESTAMP; // Date (long)
			case "guid": return java.sql.Types.CHAR; // String
			case "short": return java.sql.Types.SMALLINT; // short
			default: return 0;
		}
		// TODO - Convert this to a dictionary

		// TODO - Document "HBSQLI Type" - "Java Type" - "SQL Type"

		// A Java type or SQL type could appear multiple times, 
		// depending on how we want to interpret the value.

		// e.g. "int" = "Java int" = "sql INTEGER"
		//		"int2" = "Java int" = "sql SMALLINT"
	}

	/**
	 * Convert a scan result to a dictionary object.
	 */
	public static HBaseDictionary getDictionaryFromResult(Result r) 
		throws Exception {

		HBaseDictionary d = new HBaseDictionary();

		for (KeyValue kv:r.raw()) {
			String qualifier = new String(kv.getQualifier());
			String value = new String(kv.getValue());
			switch (qualifier) {
				case "table": 
					d.setTable(value);
					break;
				case "family": 
					d.setFamily(value);
					break;
				case "qualifier": 
					d.setQualifier(value);
					break;
				case "name": 
					d.setName(value);
					break;
				case "description": 
					d.setDescription(value);
					break;
				case "nested": 
					d.setNested(value);
					break;
				case "type": 
					d.setType(value);
					break;
				default: break;
			}
		}

		return d;
	}
}

