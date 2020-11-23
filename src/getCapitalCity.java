import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



/**
 * @author seant
 *
 */
public class getCapitalCity implements ActionListener {

	//Strings, Labels and UI stuff
    private String _resultCityName = "Please enter a country code";
    private String _title = "Standardized QA SDET Code Examination";
    private String _instructionalText = "Please enter a country code.";
    private String _buttonText = "Get Capital City";
    private JLabel _instructionalLabel = new JLabel(_instructionalText);
    private JLabel _resultLabel = new JLabel("Capital City:  " + _resultCityName);
    private JLabel _titleLabel = new JLabel(_title);
    private JFrame frame = new JFrame();
    private JTextField _inputCountry = new JTextField();
    
    //REST vars
    BufferedReader _reader;
    StringBuffer _responseData = new StringBuffer();
    String _line;
    String _countryCode = "COL";
    URL _url;
    HttpURLConnection _connection;
    int _timeout = 5000;

	public static void main(String[] args) {
		new getCapitalCity();

	}

    public getCapitalCity()
    {
    	_inputCountry.setText("COL");
    	_inputCountry.setColumns(3);
        JButton button = new JButton(_buttonText);
        button.addActionListener(this);

        // the panel with the button and text
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(25, 10, 25, 10));
        panel.setLayout(new GridLayout(0, 1));
        panel.add(_titleLabel);
        panel.add(_instructionalLabel);
        panel.add(_inputCountry);
        panel.add(button);
        panel.add(_resultLabel);

        // set up the frame and display it
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle(_title);
        frame.pack();
        frame.setVisible(true);
    }
    
    //process click events
	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			// In production, we would check for length and a valid country code
			
			_countryCode = _inputCountry.getText();
			
			_url = new URL("https://restcountries.eu/rest/v2/alpha/" + _countryCode.toLowerCase());
			
			_responseData = new StringBuffer();
			_connection = (HttpURLConnection) _url.openConnection();
			_connection.setRequestMethod("GET");
			_connection.setConnectTimeout(_timeout);
			_connection.setReadTimeout(_timeout);
			
			int returnStatus = _connection.getResponseCode();
			//anything above 299 is an error code
			if(returnStatus > 299) {
				_reader = new BufferedReader(new InputStreamReader(_connection.getErrorStream()));
				while((_line = _reader.readLine()) != null) {
					_responseData.append(_line);
				}
				_resultLabel.setText(_responseData.toString());
			}
			//all is good
			else {
				_reader = new BufferedReader(new InputStreamReader(_connection.getInputStream()));
				//TODO: PARSE THE JSON AND FIND THE CAPITAL CITY
				while((_line = _reader.readLine()) != null) {
					_responseData.append(_line);
				}
				
				JSONParser responseDataJSON = new JSONParser();
				
				
				JSONObject responseDataJSONObject = (JSONObject) responseDataJSON.parse(_responseData.toString()); 
				
				_resultCityName = (String) responseDataJSONObject.get("capital");
				
				_resultLabel.setText("Capital City:  " + _resultCityName);
				
				System.out.println(_responseData);
			}
			
			_reader.close();

			

			
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
			_resultLabel.setText("An error occurred fetching for country code " + _countryCode + ". Please correct use a valid 3 character country code.");
		}
		catch (IOException e) {
			e.printStackTrace();
			_resultLabel.setText("An IO Exception has occurred. Please check the debug logs for details.");
		} catch (ParseException e) {
			e.printStackTrace();
			_resultLabel.setText("A JSON Parsing Exception has occurred. Please check the debug logs for details.");
		}
		finally {
			_connection.disconnect();
		}

	}
	
}
