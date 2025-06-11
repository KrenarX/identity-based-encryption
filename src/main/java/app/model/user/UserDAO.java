package app.model.user;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAO {

    private final JdbcTemplate jdbcTemplate;

    public UserDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    public User createUser(User user) {
        String sql = "INSERT INTO users (username, password, info) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getInfo());
        return user;
    }

	@SuppressWarnings("deprecation")
	public User findByUsername(String username) {
    	try {
    		String sql = "SELECT * FROM users WHERE username = ?";
    		return jdbcTemplate.queryForObject(sql, new Object[]{username}, new UserRowMapper());
    	}catch(EmptyResultDataAccessException e) {
            return null;
    	}
    }
	
	 @SuppressWarnings("deprecation")
	public User findById(Long id) {
	        try {
	            String sql = "SELECT * FROM users WHERE id = ?";
	            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new UserRowMapper());
	        } catch (EmptyResultDataAccessException e) {
	            return null;
	        }
	    }

    public void delete(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
    
}


