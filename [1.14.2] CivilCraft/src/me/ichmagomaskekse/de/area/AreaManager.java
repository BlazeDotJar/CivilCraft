package me.ichmagomaskekse.de.area;

public class AreaManager {
	
	public AreaManager() {
		
	}
	
	public class Area {
		private int x_bigger = 0;
		private int y_bigger = 0;
		private int z_bigger = 0;
		private int x_smaller = 0;
		private int y_smaller = 0;
		private int z_smaller = 0;
		private String world_name = "";
		private String name = "";
		
		public Area() {
			
		}
		
		public void sortCoords() {
			int xb,yb,zb;
			if(x_bigger < x_smaller) xb = x_bigger;
			else xb = x_smaller;
			if(y_bigger < y_smaller) yb = y_bigger;
			else yb = y_smaller;
		}
	}
	
	public enum AreaType {
		AREA,
		SUB_AREA;
	}
	
}
