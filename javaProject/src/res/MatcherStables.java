package res;

import java.util.HashMap;

public class MatcherStables extends Matcher{
	
	public MatcherStables() {
		this.map = new HashMap<String, String>() {{
			   put("Promotions:", "promotion");
			   put("Years:	", "years");
			   put("Promotion:", "promotion");
			   put("Type:", "type");
			   put("Location:", "location");
			   put("Arena:", "arena");
			   put("Broadcast type:", "broadcast_type");
			   put("Broadcast date:", "broadcast_date");
			   put("TV station/network:", "network");
			   put("Commentary by:", "commentators");
			   
			   
			}};
	}
}
