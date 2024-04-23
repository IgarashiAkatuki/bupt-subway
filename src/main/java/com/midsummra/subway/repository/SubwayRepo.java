package com.midsummra.subway.repository;

import com.midsummra.subway.entity.mysql.Subway;
import org.springframework.data.repository.CrudRepository;

public interface SubwayRepo extends CrudRepository<Subway, Long> {

    Subway findAllByName(String subwayName);

}
