import org.springframework.jdbc.core.JdbcTemplate

// Place your Spring DSL code here
beans = {
	
	// uses the grails dataSource from DataSource.groovy
	jdbcTemplate(JdbcTemplate) {
	   dataSource = ref('dataSource')
	}
	
}