package com.fincity.hateoas.controller;

import com.fincity.hateoas.exception.CarNotFoundException;
import com.fincity.hateoas.model.Car;
import com.fincity.hateoas.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/cars")
public class CarController {

    @Autowired
    CarService carService;

    /**
     * Post call to create a car
     * @param car
     * @return
     * @throws CarNotFoundException
     */
    @PostMapping()
    public CollectionModel<Car> createCar(@RequestBody Car car) throws CarNotFoundException {

        carService.saveCar(car);
        Link link = linkTo(methodOn(CarController.class).getCarById(car.getId())).withRel("viewCar").withType("GET");
        Link editLink = linkTo(methodOn(CarController.class).editCar(car.getId(),car)).withRel("editCar").withType("PUT");
        car.add(Arrays.asList(link,editLink));

        Link selfLink = linkTo(methodOn(CarController.class).createCar(car)).withSelfRel().withType("POST");
        return new CollectionModel<>(Arrays.asList(car),selfLink);
    }

    /**
     * Edit Car endpoint using PUT method
     * @param id
     * @param car
     * @return
     * @throws CarNotFoundException
     */
    @PutMapping(value = "/{id}")
    public CollectionModel<Car> editCar(@PathVariable("id") Long id, @RequestBody Car car) throws CarNotFoundException {
        Car existingCar = carService.findById(id);
        if (existingCar == null)
            throw new CarNotFoundException(String.format("The requested id %s is not present", id));

        Car modifiedCar = mapValues(car,existingCar);
        carService.saveCar(modifiedCar);

        Link link = linkTo(methodOn(CarController.class).getCarById(car.getId())).withRel("viewCar").withType("GET");
        modifiedCar.add(link);

        Link selfLink = linkTo(methodOn(CarController.class).editCar(car.getId(),car)).withSelfRel().withType("PUT");
        return new CollectionModel<>(Arrays.asList(modifiedCar),selfLink);
    }


    /**
     * Edit Car endpoint using PUT method
     * @param id
     * @return
     * @throws CarNotFoundException
     */
    @DeleteMapping(value = "/{id}")
    public CollectionModel<Car> deleteCar(@PathVariable("id") Long id) throws CarNotFoundException {
        Car existingCar = carService.findById(id);
        if (existingCar == null)
            throw new CarNotFoundException(String.format("The requested id %s is not present", id));

        existingCar = carService.deleteCar(existingCar);

        Link link = linkTo(methodOn(CarController.class).getAllCars()).withRel("getAllCars").withType("GET");
        existingCar.add(link);

        Link selfLink = linkTo(methodOn(CarController.class).deleteCar(existingCar.getId())).withSelfRel().withType("DELETE");
        return new CollectionModel<>(Arrays.asList(existingCar),selfLink);
    }

    /**
     * GetAll Available cars endpoint
     * @return
     * @throws CarNotFoundException
     */
    @GetMapping(value = "/", produces = {"application/hal+json"})
    public CollectionModel<Car> getAllCars() throws CarNotFoundException {

        List<Car> cars = carService.getAllCars();
        for (Car car : cars) {
            Link selfLink = linkTo(methodOn(CarController.class).getCarById(car.getId())).withSelfRel();
            car.add(selfLink);
        }

        Car carRef = cars.get(0);

        //add self, post and query link as generic one
        Link link = linkTo(methodOn(CarController.class).getAllCars()).withSelfRel();
        Link postLink = linkTo(methodOn(CarController.class).createCar(carRef)).withRel("createCar").withType("POST");
        Link queryLink = linkTo(methodOn(CarController.class).getCarByQueryParam(carRef.getName(), carRef.getManufacturer(), carRef.getModel(), carRef.getYearOfManufacture(), carRef.getColor())).withRel("queryCar").withType("GET");
        return new CollectionModel<>(cars, Arrays.asList(link, postLink, queryLink));

    }

    /**
     * Getting a car by its id
     * @param id
     * @return
     * @throws CarNotFoundException
     */
    @GetMapping("/{id}")
    public CollectionModel<Car> getCarById(@PathVariable("id") Long id) throws CarNotFoundException {

        Car car = carService.findById(id);

        //add edit car link
        Link link = linkTo(methodOn(CarController.class).editCar(car.getId(), car)).withRel("editCar").withType("PUT");
        car.add(link);

        //self link
        Link selfLink = linkTo(methodOn(CarController.class).getCarById(car.getId())).withSelfRel().withType("GET");
        return new CollectionModel<>(Arrays.asList(car), selfLink);

    }

    /**
     * Querying a car by its param
     * None of the params required true
     * Query is constructed accordingly if the param is present or not
     * @param name
     * @param manufacturer
     * @param model
     * @param yearOfManufacture
     * @param color
     * @return
     * @throws CarNotFoundException
     */
    @GetMapping()
    public CollectionModel<Car> getCarByQueryParam(@RequestParam(value = "name", required = false) String name,
                                                   @RequestParam(value = "manufacturer", required = false) String manufacturer,
                                                   @RequestParam(value = "model", required = false) String model,
                                                   @RequestParam(value = "yearOfManufacture", required = false) Integer yearOfManufacture,
                                                   @RequestParam(value = "color", required = false) String color) throws CarNotFoundException {

        //get all cars and append self link
        List<Car> cars = carService.customSearch(name, manufacturer, model, yearOfManufacture, color);
        for (Car car : cars) {
            Link findCar = linkTo(methodOn(CarController.class).getCarById(car.getId())).withRel("getCarById").withType("GET");
            Link selfLink = linkTo(methodOn(CarController.class).getCarByQueryParam(car.getName(), car.getManufacturer(), car.getModel(), car.getYearOfManufacture(), car.getColor())).withSelfRel().withType("GET");
            car.add(Arrays.asList(findCar,selfLink));
        }

        return new CollectionModel<>(cars);
    }

    private Car mapValues(Car car, Car existingCar) {
        if (car.getName() != null)
            existingCar.setName(car.getName());
        if (car.getManufacturer() != null)
            existingCar.setManufacturer(car.getManufacturer());
        if (car.getModel() != null)
            existingCar.setModel(car.getModel());
        if (car.getColor() != null)
            existingCar.setColor(car.getColor());
        if (car.getYearOfManufacture() != null)
            existingCar.setYearOfManufacture(car.getYearOfManufacture());
        return existingCar;
    }

}
