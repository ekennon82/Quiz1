//Quiz 1 CSP443: Bryan Weaver
package motech.database;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class GetFilmsByName extends JFrame {
	private Connection connection;
	private String searchText;
	private JTextField jtfSearch;
	private JList<Film> results;
	private DefaultListModel<Film> listModel;
	private JTextArea desc;
	
	public static void main(String[] args) {
		GetFilmsByName app = new GetFilmsByName();
		app.init();
		/*try {
			app.connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
	}

	public void init() {
		try {
			initializeDB();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//Add textbox and button to execute a prepared statement
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new GridLayout(2,0));
		
		jtfSearch = new JTextField();
		JButton btnSearch = new JButton("Search");
		
		searchPanel.add(jtfSearch);
		searchPanel.add(btnSearch);
		
		results = new JList<Film>();
		listModel = new DefaultListModel<Film>();
		desc = new JTextArea();
		
		JScrollPane jsp = new JScrollPane(desc);
		JScrollPane resultsPane = new JScrollPane(results);
		
		searchPanel.add(resultsPane);
		
		//SQL statement to use:		
		//SELECT film_id, title, description
		//FROM film WHERE title LIKE %<textbox text>%
		btnSearch.addActionListener(new clickListener());
		
		//List all results
		results.addListSelectionListener(new selectListener());
		
		//Bonus points: add the results to a JList and print the 
		//film description to a text area when a film is selected
		//in the list.
		
		setLayout(new GridLayout(0,2));
		setSize(800, 480);
		setTitle("Film Finder");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		add(searchPanel);
		add(jsp);
	}

	private void initializeDB() throws SQLException, ClassNotFoundException {
		//Create database connection
		Class.forName("com.mysql.jdbc.Driver");
		System.out.println("Driver loaded");
		
		connection = DriverManager.getConnection("jdbc:mysql://localhost/sakila", "user1", "password");
		System.out.println("Database connected");
	}
	
	public class clickListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			searchText = jtfSearch.getText();
			Statement films;
			try {
				films = connection.createStatement();
				ResultSet filmsResult = films.executeQuery("SELECT film_id, title, description FROM film WHERE title LIKE '%" + searchText + "%'");
				
				while (filmsResult.next()) {
					Film film = new Film();
					film.setFilmId(filmsResult.getInt(1));
					film.setFilmName(filmsResult.getString(2));
					film.setDescription(filmsResult.getString(3));
					listModel.addElement(film);
				}
				results.setModel(listModel);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public class selectListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			desc.setText(results.getSelectedValue().getDescription());
		}
		
	}
}
