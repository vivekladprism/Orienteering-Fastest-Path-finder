import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.imageio.ImageIO;


class Pixel
{
	public int xCord;
	public int yCord;
	public int layer = 0;
	public double elevationHeight;
	public Terrain terrain;
	public Map<Pixel,Double> neighbours;
	
	public Pixel()
	{
		
	}
	public Pixel(int xCord, int yCord, Terrain terrain)
	{
		this.xCord = xCord;
		this.yCord = yCord;
		this.terrain = terrain;
		neighbours = new HashMap<>();
		
	}
	public String toString()
	{	
		String n = "";
		for(Pixel p : neighbours.keySet())
		{
			n = n+ "(" + p.xCord + "," + p.yCord + ")" + "  ";
		}
		return "(" + xCord + "," + yCord + ")" + " -> "+ terrain + "  " + elevationHeight + " --> " + n;
	}
	
	public void addNeighbour(Pixel p, double speed)
	{
		if(p.elevationHeight - this.elevationHeight > 0)
			speed = speed - (p.elevationHeight - this.elevationHeight) / 100;
		else if(p.elevationHeight - this.elevationHeight < 0)
			speed = speed + (this.elevationHeight - p.elevationHeight) / 100;
		
		neighbours.put(p, speed);
	}
}

class Terrain
{
	String terrainName;
	Color terrainColor;
	double terrainSpeed;
	
	public Terrain(String terrainName, Color terrainColor, double terrainSpeed)
	{
		this.terrainColor = terrainColor;
		this.terrainName = terrainName;
		this.terrainSpeed = terrainSpeed;
	}
	public String toString()
	{
		return "terrainName = " + terrainName + "   " + terrainColor + "    " + terrainSpeed;
	}
}

public class Orienteering
{
	public static String terrainImageFile;
	public static String elevationFile;
	public static String pathFile;
	public static String seasonName;
	public static String outputImageFile;
	public static BufferedImage bi;
	public static int imageWidth;
	public static int imageHeight;
	public static double latitudeDistance = 7.55;
	public static double longitudeDistance = 10.29;
	public static List<Pixel> pointsToTravel = new ArrayList<>();
	public static Map<String,Pixel> pixelLocations = new HashMap<>();
	public static Map<String,Terrain> terrainData = new HashMap<>();
	public static double totalDistance;
	public static BufferedImage bi2;
	
	public static void readInput(String[] args) 
	{
		terrainImageFile = args[0];
		elevationFile = args[1];
		pathFile = args[2];
		seasonName = args[3].toLowerCase();
		outputImageFile = args[4];

	}
	public static void readTerrainImage() throws IOException
	{
		File file = new File(terrainImageFile);
		imageWidth = 395;
		imageHeight = 500;
		
		bi = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		bi = ImageIO.read(file);
		
	}
	
	public static void setTerrainAndSpeeds()
	{
		Terrain terrain = new Terrain("Open land", new Color(248, 148, 18), 14.5);
		terrainData.put("Open land", terrain);

		terrain = new Terrain("Paved road", new Color(71, 51, 3), 20);
		terrainData.put("Paved road", terrain);

		terrain = new Terrain("Footpath", new Color(0, 0, 0), 17);
		terrainData.put("Footpath", terrain);

		terrain = new Terrain("Easy movement forest", new Color(255, 255, 255), 12);
		terrainData.put("Easy movement forest", terrain);

		terrain = new Terrain("Rough meadow", new Color(255, 192, 0), 9);
		terrainData.put("Rough meadow", terrain);

		terrain = new Terrain("Slow run forest", new Color(2, 208, 60), 6.5);
		terrainData.put("Slow run forest", terrain);

		terrain = new Terrain("Walk forest", new Color(2, 136, 40), 4.2);
		terrainData.put("Walk forest", terrain);

		terrain = new Terrain("Impassible vegetation", new Color(5, 73, 24), 2);
		terrainData.put("Impassible vegetation", terrain);

		terrain = new Terrain("Lake/Swamp/Marsh", new Color(0, 0, 255), 0.5);
		terrainData.put("Lake/Swamp/Marsh", terrain);

		terrain = new Terrain("Out of bounds", new Color(205, 0, 101), 0.1);
		terrainData.put("Out of bounds", terrain);
			
	}
	
	public static void readAndCreatePixels() throws IOException
	{
		File file = new File(elevationFile);
		BufferedReader br = new BufferedReader(new FileReader(file));
		bi2 = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		for(int i = 0 ; i < imageHeight; i ++)
		{
			String s[] = br.readLine().trim().split("\\s+");
			char c = 'a';
			for(int col= 0 ; col< imageWidth; col++)
			{
				Color color = new Color(bi.getRGB(j, i));
				
				int red = color.getRed();
				int green = color.getGreen();
				int blue = color.getBlue();

				Terrain terrain = null;
				Pixel pixel;
				if(red == 248  && green == 148 && blue == 18)
				{
					terrain = terrainData.get("Open land");
				}
				else if(red == 255  && green == 192 && blue == 0)
				{
					terrain = terrainData.get("Rough meadow");
				}
				else if(red == 255  && green == 255  && blue == 255)
				{
					terrain = terrainData.get("Easy movement forest");
				}
				else if(red == 2 && green == 208 && blue == 60)
				{
					terrain = terrainData.get("Slow run forest");
				}
				else if(red == 2 && green == 136 && blue == 40)
				{
					terrain = terrainData.get("Walk forest");
				}
				else if(red == 5  && green == 73 && blue == 24)
				{
					terrain = terrainData.get("Impassible vegetation");
				}
				else if(red == 0 && green == 0 && blue == 255)
				{
					terrain = terrainData.get("Lake/Swamp/Marsh");
				}
				else if(red == 71 && green == 51 && blue == 3)
				{
					terrain = terrainData.get("Paved road");
				}
				else if(red == 0 && green == 0 && blue == 0)
				{
					terrain = terrainData.get("Footpath");
				}
				else if(red == 205 && green == 0 && blue == 101)
				{
					terrain = terrainData.get("Out of bounds");
				}

				pixel = new Pixel(i, j, terrain);
				pixelLocations.put("(" + i + "," + col+ ")", pixel);
				pixel.elevationHeight = Double.parseDouble(s[j]);
				
			}
		}
	}
	
	public static void printPixelData()
	{
		for(int row = 0 ; row < imageHeight; row ++)
		{
			for(int col = 0 ; col < imageWidth; col ++)
			{
				System.out.println(pixelLocations.get("(" + row + "," + col + ")"));
			}
		}
	}
	public static void addPixelNeighbours()
	{
		for(int row = 0; row < imageHeight; row++)
		{
			for(int col = 0 ; col < imageWidth; col++)
			{
				Pixel pixel = pixelLocations.get("(" + row + "," + col + ")");
				Pixel neighbour;
				
				if (row < imageHeight - 1)
				{
					neighbour = pixelLocations.get("(" + (row + 1) + "," + col + ")");
					pixel.addNeighbour(neighbour, neighbour.terrain.terrainSpeed);

				}
				if (row > 0)
				{
					neighbour = pixelLocations.get("(" + (row - 1) + "," + col+ ")");
					p.addNeighbour(neighbour, neighbour.terrain.terrainSpeed);

				}
				if (row < imageHeight - 1 && col< imageWidth - 1)
				{
					neighbour = pixelLocations.get("(" + (row + 1) + "," + (col+ 1) + ")");
					p.addNeighbour(neighbour, neighbour.terrain.terrainSpeed);
				}

				if (row < imageHeight - 1 && col> 0)
				{
					neighbour = pixelLocations.get("(" + (row + 1) + "," + (col- 1) + ")");
					p.addNeighbour(neighbour, neighbour.terrain.terrainSpeed);
				}

				if (col> 0)
				{
					neighbour = pixelLocations.get("(" + row + "," + (col- 1) + ")");
					p.addNeighbour(neighbour, neighbour.terrain.terrainSpeed);
				}

				if (row > 0 && col< imageWidth - 1)
				{
					neighbour = pixelLocations.get("(" + (row - 1) + "," + (col+ 1) + ")");
					p.addNeighbour(neighbour, neighbour.terrain.terrainSpeed);
				}

				if (row > 0 && col> 0)
				{
					neighbour = pixelLocations.get("(" + (row - 1) + "," + (col- 1) + ")");
					p.addNeighbour(neighbour, neighbour.terrain.terrainSpeed);
				}

				if (col< imageWidth - 1)
				{
					neighbour = pixelLocations.get("(" + row + "," + (col+ 1) + ")");
					p.addNeighbour(neighbour, neighbour.terrain.terrainSpeed);
				}	
			}
		}
	}
	
	public static void readPointsToTravel() throws IOException
	{
		File file = new File(pathFile);
		BufferedReader br = new BufferedReader(new FileReader(pathFile));
		String line;
		while((line = br.readLine()) != null)
		{
			String[] points = line.trim().split("\\s+");
			
			int row = Integer.parseInt(points[0]);
			int col = Integer.parseInt(points[1]);
		
			pointsToTravel.add(pixelLocations.get("(" + col+ "," + row + ")")) ;
		}
	}
	
	public static void displayPointsToTravel()
	{
		for(Pixel pixel : pointsToTravel)
		{
			System.out.println(pixel.xCord + " " + pixel.yCord);
		}
	}
	
	public static void findPathToAllPoints()
	{
		System.out.println(" ***** Human Readable Output ***** ");
		System.out.println();
		System.out.println("take the following path");
		Pixel startingPoint = pointsToTravel.get(0);
		
		for(int point = 1; point < pointsToTravel.size(); point ++)
		{
			
			Map<Pixel, Pixel> map;
			Pixel nextPoint = pointsToTravel.get(point);
			
			if(nextPoint.terrain.terrainName.equals("Out of bounds"))
				continue;
			
			map = runAStarAlgorithm(startingPoint, nextPoint);
			startingPoint = nextPoint;
		}
	}
	public static Map<Pixel, Pixel> runAStarAlgorithm(Pixel start, Pixel end)
	{
		Map<Pixel, Pixel> previousPixel = new HashMap<>();
		Set<Pixel> visited = new HashSet<>();
		while(start != end)
		{
			int startX = start.xCord;
			int startY = start.yCord;
			double minTime = Double.MAX_VALUE - 1;
			double time = 0;
			Pixel nextPixel = null;
			visited.add(start);
			for(Pixel p : start.neighbours.keySet())
			{
				if(!visited.contains(p))
				{
					time = g(start, p) + heuristicFunction(p, end);
					
					if(time < minTime)
					{
						nextPixel = p;
						minTime = time;
					}
				}
			}
			Color colr  = new Color(158, 2, 4);
			if(nextPixel == null)
			{
				nextPixel = goToNearestPixelInPath(start, end);
				
				bi.setRGB(nextPixel.yCord, nextPixel.xCord, colr.getRGB());
				while(visited.contains(nextPixel))
				{
					nextPixel = goToNearestPixelInPath(nextPixel, end);
					if(nextPixel.terrain.terrainName.equals("Out of bounds"))
						continue;

					bi.setRGB(nextPixel.yCord, nextPixel.xCord, colr.getRGB());
				}
			}
			System.out.println(start.xCord + "," + start.yCord + " - - > " + nextPixel.xCord + "," + nextPixel.yCord);
			visited.add(nextPixel);
			bi.setRGB(nextPixel.yCord, nextPixel.xCord, colr.getRGB());
			
			int nextPixelX = nextPixel.xCord;
			int nextPixelY = nextPixel.yCord;
			
			previousPixel.put(nextPixel, start);
			double distance = 0;
			if(startX == nextPixelX && startY-nextPixelY == 1 || nextPixelY - startY == 1)
			{
				distance = latitudeDistance;  
			}
			else if(startY == nextPixelY && startX - nextPixelX == 1 || nextPixelX - startX == 1)
			{
				distance = longitudeDistance; 
			}
			else
			{
				distance = Math.sqrt(Math.pow(longitudeDistance, 2) + Math.pow(latitudeDistance, 2));
				
			}
			totalDistance = totalDistance + distance;
			start = nextPixel;
			
		}
		System.out.println("reached destination");
		return previousPixel;
	
	}
	
	public static double heuristicFunction(Pixel start, Pixel end)
	{
		double horizontal = Math.pow(start.xCord - end.xCord, 2)+Math.pow(start.yCord - end.yCord, 2);
		double vertical = Math.pow(start.elevationHeight - end.elevationHeight,2);
		return Math.sqrt(vertical + horizontal);
	}
	public static Pixel goToNearestPixelInPath(Pixel start, Pixel end)
	{
		int x = start.xCord;
		int y = start.yCord;
		int x2 = end.xCord;
		int y2 = end.yCord;
		
		int dirx = x2 - x;
		int diry = y2 - y;
		
		double rad = Math.atan2(diry, dirx);
		double degree = Math.toDegrees(rad);
		
		
		if(degree < 0)
			degree = 360 - Math.abs(degree);
		
		if((degree >= 0 && degree < 45) || (degree > 315) )
		{
			start = pixelLocations.get("(" + (x + 1) + "," + y + ")");
		}
			
		else if(degree == 45)
			start = pixelLocations.get("(" + (x + 1) + "," + (y + 1) + ")");
		else if(degree > 45 && degree < 135)
			start = pixelLocations.get("(" + (x) +"," + (y + 1) + ")");
		else if(degree == 135)
			start = pixelLocations.get("(" + (x) + "," + (y + 1) + ")");
		else if(degree > 135 && degree < 225)
			start = pixelLocations.get("(" + (x - 1) + "," + (y) + ")");
		else if(degree == 225)
			start = pixelLocations.get("(" + (x - 1) + "," + (y - 1) + ")");
		else if(degree > 225 && degree < 315)
		{
			start = pixelLocations.get("(" + x + "," + (y - 1) +")");
		}
		else
			start = pixelLocations.get("(" + ( x + 1 ) + "," + (y - 1) + ")");
		
		return start;
		
	}
	public static double g(Pixel start, Pixel end)
	{
		int x1 = start.xCord;
		int y1 = start.yCord;
		int x2 = end.xCord;
		int y2 = end.yCord;
		double cost = 0;
		if(x1 == x2 && y1 - y2 == 1 || y2 - y1 == 1)
		{
			cost = (latitudeDistance / 2) / start.terrain.terrainSpeed + (latitudeDistance / 2) / start.neighbours.get(end);  
		}
		else if(y1 == y2 && x1 - x2 == 1 || x2 - x1 == 1)
		{
			cost = (longitudeDistance / 2) / start.terrain.terrainSpeed + (longitudeDistance / 2) / start.neighbours.get(end); 
		}
		else
		{
			double distance = Math.sqrt(Math.pow(longitudeDistance, 2) + Math.pow(latitudeDistance, 2));
			cost = (distance / 2) / start.terrain.terrainSpeed + (distance / 2) / start.neighbours.get(end);
		}
		return cost;
	}
	
	public static void tracePath(Map<Pixel, Pixel> previous, Pixel start, Pixel end)
	{
		while(end != start)
		{
			Color colr  = new Color(102, 0, 102);
			bi.setRGB(end.yCord, end.xCord, colr.getRGB());
		
			end = previous.get(end);
		}
	}
	
	public static void performSeasonVariations()
	{
		if(seasonName.equals("summer"))
		{
			return;
		}
		
		else if(seasonName.equals("fall"))
		{
			Terrain t = terrainData.get("Easy movement forest");
			t.terrainSpeed = t.terrainSpeed - (1/5) * t.terrainSpeed;
		}
		else if(seasonName.equals("winter"))
		{
			makeWinterChanges();
		}
		else if(seasonName.equals("spring"))
		{
			makeSpringChanges();
		}
	}
	
	public static void makeWinterChanges()
	{
		Set<Pixel> waterEdges = new HashSet<>();
		Set<Pixel> waterBody = new HashSet<>();
		for(int row = 1; row < imageHeight - 1 ; row ++)
		{
			for(int col= 1; col< imageWidth - 1; col++)
			{
				Pixel p = pixelLocations.get("(" + (i) + "," + (j) + ")");
				if ( p.terrain.terrainName.equals("Lake/Swamp/Marsh"))
					waterBody.add(p);
				else continue;
				Pixel q;
				q = pixelLocations.get("(" + (row + 1) + "," + (j) + ")");
				if ( !q.terrain.terrainName.equals("Lake/Swamp/Marsh"))
					waterEdges.add(p);
				q = pixelLocations.get("(" + (row - 1) + "," + (j) + ")");
				if ( !q.terrain.terrainName.equals("Lake/Swamp/Marsh"))
					waterEdges.add(p);
				q = pixelLocations.get("(" + (i) + "," + (col+ 1) + ")");
				if ( !q.terrain.terrainName.equals("Lake/Swamp/Marsh"))
					waterEdges.add(p);
				q = pixelLocations.get("(" + (i) + "," + (col- 1) + ")");
				if ( !q.terrain.terrainName.equals("Lake/Swamp/Marsh"))
					waterEdges.add(p);
			}
		}
		
		Terrain t = new Terrain("Ice", new Color(115, 255, 248), 3);
	
		for(Pixel pi : waterEdges)
		{
			pi.layer = 0;
			Queue<Pixel> q = new LinkedList<>();
			q.add(pi);
			Set<Pixel> visited = new HashSet<>();
			if(pi.terrain.terrainName.equals("Lake/Swamp/Marsh"))
				pi.terrain = t;
			
			boolean depthReached = false;
			
			while(!q.isEmpty())
			{
				Pixel start = q.poll();
				
				
				for(Pixel z : start.neighbours.keySet())
				{
					if(!visited.contains(z) && waterBody.contains(z))
					{
						z.layer = start.layer + 1;
						if(z.layer >=  7)
						{
							depthReached = true;
							break;
						}
						q.add(z);
						visited.add(z);
						z.terrain = t;
						bi.setRGB(z.yCord, z.xCord, t.terrainColor.getRGB());
					}
				}
				
				if(depthReached)
					break;
				
			}
		}
	}
	
	public static void makeSpringChanges()
	{
		Set<Pixel> waterEdges = new HashSet<>();
		Set<Pixel> waterBody = new HashSet<>();
		for(int i = 1; i < imageHeight - 1 ; i++)
		{
			for(int col= 1; col< imageWidth - 1; j++)
			{
				Pixel p = pixelLocations.get("(" + (i) + "," + (j) + ")");
				if ( p.terrain.terrainName.equals("Lake/Swamp/Marsh"))
					waterBody.add(p);
				else continue;
				Pixel q;
				q = pixelLocations.get("(" + (i + 1) + "," + (j) + ")");
				if ( !q.terrain.terrainName.equals("Lake/Swamp/Marsh"))
					waterEdges.add(p);
				q = pixelLocations.get("(" + (i - 1) + "," + (j) + ")");
				if ( !q.terrain.terrainName.equals("Lake/Swamp/Marsh"))
					waterEdges.add(p);
				q = pixelLocations.get("(" + (i) + "," + (col+ 1) + ")");
				if ( !q.terrain.terrainName.equals("Lake/Swamp/Marsh"))
					waterEdges.add(p);
				q = pixelLocations.get("(" + (i) + "," + (col- 1) + ")");
				if ( !q.terrain.terrainName.equals("Lake/Swamp/Marsh"))
					waterEdges.add(p);
			}
		}
		
		Terrain t = new Terrain("Mud", new Color(139, 69, 19), 1);		
		
		for(Pixel pi : waterEdges)
		{
			pi.layer = 0;
			Queue<Pixel> q = new LinkedList<>();
			q.add(pi);
			Set<Pixel> visited = new HashSet<>();
			if(pi.terrain.terrainName.equals("Lake/Swamp/Marsh"))
				pi.terrain = t;
			
			double elevation = pi.elevationHeight;
			boolean depthReached = false;
			
			while(!q.isEmpty())
			{
				Pixel start = q.poll();
					
				for(Pixel z : start.neighbours.keySet())
				{
					if(!visited.contains(z) && !waterBody.contains(z))
					{
						
						z.layer = start.layer + 1;
						if(z.layer >=  15)
						{
							
							depthReached = true;
							break;
						}
						
						if(!z.terrain.terrainName.equals("Out of bounds"))
						{
							z.terrain = t;
							bi.setRGB(z.yCord, z.xCord, t.terrainColor.getRGB());
						}
						if(z.elevationHeight - start.elevationHeight <= 1)
						{
							q.add(z);
						
						}
						visited.add(z);

					}
				}
				
				if(depthReached)
					break;
				
			}
		}
	}
	public static void paintPointsTobeVisited()
	{
		Color c = new Color(91, 12, 117);
		for(Pixel p : pointsToTravel)
		{
			bi.setRGB(p.yCord, p.xCord, c.getRGB());
			bi.setRGB(p.yCord + 1, p.xCord - 1, c.getRGB());
			bi.setRGB(p.yCord + 1, p.xCord + 1, c.getRGB());
			bi.setRGB(p.yCord, p.xCord + 1, c.getRGB());
			bi.setRGB(p.yCord, p.xCord - 1, c.getRGB());
			bi.setRGB(p.yCord - 1, p.xCord + 1, c.getRGB());
			bi.setRGB(p.yCord - 1, p.xCord - 1, c.getRGB());
			bi.setRGB(p.yCord + 1, p.xCord, c.getRGB());
			bi.setRGB(p.yCord - 1, p.xCord, c.getRGB());
		}
	}
	public static void writeOutputImage() throws IOException
	{
		File file = new File(outputImageFile);
		
		ImageIO.write(bi, "png", file);
	}
	
	public static void main(String[] args) throws IOException
	{
		readInput(args);
		
		readTerrainImage();
		
		setTerrainAndSpeeds();
		
		readAndCreatePixels();
		
		addPixelNeighbours();
		
		performSeasonVariations();
		
		writeOutputImage();
		
		readPointsToTravel();
		
		findPathToAllPoints();
		
		paintPointsTobeVisited();
		
		System.out.println("Total Distance Travelled :" + totalDistance + " meters");
		
		writeOutputImage();
	}

}
