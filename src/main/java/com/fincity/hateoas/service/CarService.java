package com.fincity.hateoas.service;

import com.fincity.hateoas.model.Car;
import com.fincity.hateoas.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    public List<Car> getAllCars() {
        return (List<Car>)carRepository.findAll();
    }

    public void saveCar(Car car) {
        if(car != null)
            carRepository.save(car);
    }

    public void editCar(Car car) {
        if (car != null)
            carRepository.save(car);
    }

    public Car findById(Long id) {
        Car car = null;
        if(id != 0){
            car = Optional.ofNullable(carRepository.findById(id)).orElse(null);
        }
        return car;
    }

    public Car deleteCar(Car car){
        carRepository.delete(car);
        return car;
    }

    public List<Car> customSearch(String name, String manufacturer, String model, Integer yearOfManufacture, String color) {
        return carRepository.customQuery(name,model,manufacturer,yearOfManufacture,color);
    }
}
