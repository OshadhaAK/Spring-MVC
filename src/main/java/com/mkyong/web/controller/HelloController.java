package com.mkyong.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Controller
public class HelloController {

	@Autowired
	private DataSource dataSource;
	ArrayList<User> result = new ArrayList();
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String printWelcome(ModelMap model) {

		model.addAttribute("message", new User());
		return "hello";

	}

	@RequestMapping(value = "/hello/{name:.+}", method = RequestMethod.GET)
	public ModelAndView hello(@PathVariable("name") String name) {

		ModelAndView model = new ModelAndView();
		model.setViewName("hello");
		model.addObject("msg", name);

		return model;

	}

	@RequestMapping(value = "/result", method = RequestMethod.POST)
	public String formSubmit(@ModelAttribute("message") User user, Model model) {
		// save data to database
		// reetrive all from database
		try {
			Connection conn = dataSource.getConnection();
			if (conn != null) {
				System.out.println("Connected to the database!");
			} else {
				System.out.println("Failed to make connection!");
			}
			String sql = "INSERT INTO example(firstname,lastname,email) "
					+ "VALUES (?,?,?)";

			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1,user.getFname());
			preparedStatement.setString(2,user.getLname());
			preparedStatement.setString(3,user.getEmail());

			preparedStatement.execute();
			preparedStatement.close();
			String query = "SELECT * FROM example";

			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);

			while (rs.next())
			{
				String fname = rs.getString("firstname");
				String lname = rs.getString("lastname");
				String email = rs.getString("email");

				User obj = new User();
				obj.setFname(fname);
				obj.setLname(lname);
				obj.setEmail(email);

				result.add(obj);



			}
			model.addAttribute("message", result);
			st.close();
			conn.close();


		} catch (SQLException e) {
			System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}


		//model.addAttribute("message", user);
		return "result";
	}

	/*@GetMapping(value = "/result")
	public String getResult(ModelMap model) {
		model.addAttribute("message", new User());
		return "result";
	}*/

}