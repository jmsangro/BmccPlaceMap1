package application;

import java.util.Collection;
import java.util.SortedSet;

public interface DataSource {
	
	SortedSet<String> getSirNames();
	SortedSet<String> getTownNames();
	NamedPoint getUSLocation(String townName);
	Collection<String> getUSLocBySirName(String value);
	Collection<OrigDestPair> getOrigDestPairBySirName(String sirName);
	NamedPoint getEusLocation(String townName);
	Collection<OrigDestPair> getOrigDestPairsByTownName(String town);
	Mapper getUSMapper();
	Mapper getEusMapper();
	

}
