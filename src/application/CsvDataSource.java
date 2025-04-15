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
	private static final int SIR_NAME_INDEX=0;
	private static final int ORIGIN_INDEX=1;
	private static final int DESTINATION_INDEX=2;
	private static final int LOC_Y_INDEX = 1;
	private static final int LOC_X_INDEX = 2;
	private static final int LOC_NAME_INDEX = 0;
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
				validateData();
			}
	        

		}
		catch (Exception e) {
			System.err.println(e);
		}
	}

	private void validateData() {
		for (String[] row : records) {
			String familyName = row[SIR_NAME_INDEX];
			String usTownName = row[DESTINATION_INDEX];
			String eusTownName = row[ORIGIN_INDEX];
			NamedPoint point = getUSLocation(usTownName);
			if (point == null || point.getName()==null || point.getX() == 0 || point.getY() == 0) {
				System.err.println("Validating family:"+familyName+" US location data for town:"+usTownName+ " is bad. point info="+point);
			}
			point = getEusLocation(eusTownName);
			if (point == null || point.getName()==null || point.getX() == 0 || point.getY() == 0) {
				System.err.println("Validating family:"+familyName+" Euskadi location data for town:"+eusTownName+ " is bad. point info="+point);
			}
		}
		
	}

	@Override
	public SortedSet<String> getSirNames() {
		if (sirNames == null) {
			sirNames = new TreeSet<String>();
			for(String[] record : records){
				sirNames.add(record[SIR_NAME_INDEX]);
			}
		}
		return sirNames;
	}

	@Override
	public SortedSet<String> getTownNames() {
		if (homeTownNames == null) {
			homeTownNames = new TreeSet<String>();
			for(String[] record : records){
				homeTownNames.add(record[ORIGIN_INDEX]);
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
					point.setName(row[LOC_NAME_INDEX]);
					point.setX(Double.parseDouble(row[LOC_X_INDEX]));
					point.setY(Double.parseDouble(row[LOC_Y_INDEX]));
					usLocations.put(point.getName(), point);
					//System.out.println(usLocations.get(point.getName()));
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
					point.setName(row[LOC_NAME_INDEX]);
					point.setX(Double.parseDouble(row[LOC_X_INDEX]));
					point.setY(Double.parseDouble(row[LOC_Y_INDEX]));
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
			if (sirName.equals(record[SIR_NAME_INDEX])) {
				addPairFromRecord(sirName, odPairs, record);
			}
		}
		return odPairs;
	}

	private void addPairFromRecord(String sirName, SortedSet<OrigDestPair> odPairs, String[] record) {
		String origName = record[ORIGIN_INDEX];
		String destName = record[DESTINATION_INDEX];
		NamedPoint origPoint = getEusLocation(origName);
		NamedPoint destPoint = getUSLocation(destName);
		OrigDestPair odPair = new OrigDestPair(sirName, origPoint, destPoint);
		odPairs.add(odPair);
	}

	@Override
	public Collection<OrigDestPair> getOrigDestPairsByTownName(String town) {
		SortedSet<OrigDestPair> odPairs = new TreeSet<OrigDestPair>();
		for (String[] record : records) {
			if (town.equals(record[ORIGIN_INDEX])) {
				String sirName = record[SIR_NAME_INDEX];
				addPairFromRecord(sirName, odPairs, record);
			}
		}
		return odPairs;
	}

}
