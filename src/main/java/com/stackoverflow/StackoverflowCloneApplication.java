package com.stackoverflow;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@SpringBootApplication
public class StackoverflowCloneApplication {

	public static void main(String[] args) {
		SpringApplication.run(StackoverflowCloneApplication.class, args);
	}

	@Bean
	ModelMapper getmodelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		return modelMapper;
	}

	public static String formatTime(LocalDateTime dateTime) {
		LocalDateTime now = LocalDateTime.now();

		if (dateTime.toLocalDate().equals(now.toLocalDate())) {
			long minutes = ChronoUnit.MINUTES.between(dateTime, now);
			long hours = ChronoUnit.HOURS.between(dateTime, now);

			if (minutes < 1) {
				return "Just now";
			} else if (minutes < 60) {
				return minutes + " mins ago";
			} else {
				return hours + " hours ago";
			}
		} else {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm");
			return "Answered " + dateTime.format(formatter);
		}
	}

}
