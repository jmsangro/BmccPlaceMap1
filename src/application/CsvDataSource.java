package application;

import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.opencsv.CSVReader;

public class CsvDataSource implements DataSource {
	
	private List<String[]> records;
	private SortedSet<String> sirNames;
	private SortedSet<String> homeTownNames;
	private Map<String, NamedPoint> usLocations;
	private Map<String, NamedPoint> eusLocations;
	
	public CsvDataSource() {
		try {
			String dataFile = "./FamilyLocationData.csv";
	        FileReader filereader = new FileReader(dataFile, StandardCharsets.UTF_8);
	        try (CSVReader csvReader = new CSVReader(filereader)) {
				records = csvReader.readAll();
				//remove title row.
				records.remove(records.get(0));
			}

		}
		catch (Exception e) {
			System.err.println(e);
		}
	}

	@Override
	public SortedSet<String> getSirNames() {
		if (sirNames == null) {
			sirNames = new TreeSet<String>();
			for(String[] record : records){
				sirNames.add(record[0]);
			}
		}
		return sirNames;
	}

	@Override
	public SortedSet<String> getTownNames() {
		if (homeTownNames == null) {
			homeTownNames = new TreeSet<String>();
			for(String[] record : records){
				homeTownNames.add(record[1]);
			}
		}
		return homeTownNames;
	}

	@Override
	public NamedPoint getUSLocation(String townName) {
		if (usLocations == null) {
			readUSLocations();
		}
		return usLocations.get(townName);
	}
	
	@Override
	public NamedPoint getEusLocation(String townName) {
		if (eusLocations == null) {
			readEusLocations();
		}
		return eusLocations.get(townName);
		
	}

	private void readUSLocations() {
		usLocations = new HashMap<String, NamedPoint>();
		try {
			String dataFile = "./USLocationLatLong.csv";
	        FileReader filereader = new FileReader(dataFile, StandardCharsets.UTF_8); 
	        
	        try (CSVReader csvReader = new CSVReader(filereader)) {
				List<String[]> rawData = csvReader.readAll();
				for (String[] row : rawData) {
					NamedPoint point = new NamedPoint();
					point.setName(row[0]);
					point.setX(Double.parseDouble(row[2]));
					point.setY(Double.parseDouble(row[1]));
					usLocations.put(point.getName(), point);
					System.out.println(usLocations.get(point.getName()));
				}
				
			}

		}
		catch (Exception e) {
			System.err.println(e);
		}		
	}
	
	private void readEusLocations() {
		eusLocations = new HashMap<String, NamedPoint>();
		try {
			String dataFile = "./EusLocationLatLongProvinceUTF8.csv";
	        FileReader filereader = new FileReader(dataFile, StandardCharsets.UTF_8); 
	        System.out.println(filereader.getEncoding());

	        try (CSVReader csvReader = new CSVReader(filereader)) {
				List<String[]> rawData = csvReader.readAll();
				for (String[] row : rawData) {
					NamedPoint point = new NamedPoint();
					point.setName(row[0]);
					point.setX(Double.parseDouble(row[2]));
					point.setY(Double.parseDouble(row[1]));
					eusLocations.put(point.getName(), point);
				}
				
			}

		}
		catch (Exception e) {
			System.err.println(e);
		}		
	}
	
	
	@Override
	public Collection<String> getUSLocBySirName(String sirName) {
		List<String> townNames = new ArrayList<String>();
		for (String[] record : records) {
			if (sirName.equals(record[0])) {
				townNames.add(record[2]);
			}
		}
		return townNames;
	}
	
	@Override
	public Collection<OrigDestPair> getOrigDestPairBySirName(String sirName) {
		SortedSet<OrigDestPair> odPairs = new TreeSet<OrigDestPair>();
		for (String[] record : records) {
			if (sirName.equals(record[0])) {
				String origName = record[1];
				String destName = record[2];
				NamedPoint origPoint = getEusLocation(origName);
				NamedPoint destPoint = getUSLocation(destName);
				OrigDestPair odPair = new OrigDestPair(sirName, origPoint, destPoint);
				odPairs.add(odPair);
			}
		}
		return odPairs;
	}

	@Override
	public Collection<OrigDestPair> getOrigDestPairsByTownName(String town) {
		SortedSet<OrigDestPair> odPairs = new TreeSet<OrigDestPair>();
		for (String[] record : records) {
			if (town.equals(record[1])) {
				String sirName = record[0];
				String origName = record[1];
				String destName = record[2];
				NamedPoint origPoint = getEusLocation(origName);
				NamedPoint destPoint = getUSLocation(destName);
				OrigDestPair odPair = new OrigDestPair(sirName, origPoint, destPoint);
				odPairs.add(odPair);
			}
		}
		return odPairs;
	}

}
