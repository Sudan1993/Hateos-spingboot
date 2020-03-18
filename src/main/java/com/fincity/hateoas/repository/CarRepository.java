package com.fincity.hateoas.repository;

import com.fincity.hateoas.model.Car;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Id;
import java.util.List;

@Repository
public interface CarRepository extends CrudRepository<Car, Id> {

    Car findById(Long id);

    /**
     * Ability to search car/cars by name , manufacture name , model , manufacturing year
     * and/or color.
     * Null query params will be ignored
     * @param name
     * @param model
     * @param manufacturer
     * @param yearOfManufacture
     * @param color
     * @return
     */
    @Query("SELECT u FROM Car u where (:name is null or u.name = :name) and (:model is null or u.model = :model) and " +
                                    "(:manufacturer is null or u.manufacturer = :manufacturer) and (:yearOfManufacture is null or u.yearOfManufacture = :yearOfManufacture) and" +
                                    "(:color is null or u.color = :color)")
    List<Car> customQuery(@Param("name") String name, @Param("model") String model, @Param("manufacturer") String manufacturer, @Param("yearOfManufacture") Integer yearOfManufacture, @Param("color") String color);
}
