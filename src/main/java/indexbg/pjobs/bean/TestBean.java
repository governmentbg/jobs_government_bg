package indexbg.pjobs.bean;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Named;

@Named("testBean")
public class TestBean {
	
	private List<Car> cars;

	public TestBean() {
		super();
	}
	
	@PostConstruct
	public void init() {
		createCars();
	}
	
	private void createCars() {
		this.cars = new ArrayList<Car>();
		cars.add(new Car("1", "1999", "Skoda", "red"));
		cars.add(new Car("2", "2003", "Audi", "green"));
		cars.add(new Car("3", "2017", "Opel", "blue"));
		cars.add(new Car("4", "1965", "Volkswagen", "yellow"));
		cars.add(new Car("5", "2008", "Mercedes", "black"));
		cars.add(new Car("6", "1994", "BMW", "silver"));
	}

	public List<Car> getCars() {
		return cars;
	}

	public void setCars(List<Car> cars) {
		this.cars = cars;
	}

	public class Car {
		private String id;
		private String year;
		private String brand;
		private String color;
		
		public Car(String id, String year, String brand, String color) {
			super();
			this.id = id;
			this.year = year;
			this.brand = brand;
			this.color = color;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getYear() {
			return year;
		}

		public void setYear(String year) {
			this.year = year;
		}

		public String getBrand() {
			return brand;
		}

		public void setBrand(String brand) {
			this.brand = brand;
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}
		
		
	}
}
