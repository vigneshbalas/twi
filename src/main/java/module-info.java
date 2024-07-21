module twi {
	exports com.vigneshbala.twi.util;
	exports com.vigneshbala.twi.nlp;
	exports com.vigneshbala.twi.cli;
	exports com.vigneshbala.twi.model;

	requires info.picocli;
	requires jul.to.slf4j;
	requires org.apache.commons.lang3;
	requires org.slf4j;
	requires org.json;
}