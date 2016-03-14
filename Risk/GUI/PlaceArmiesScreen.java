/**
 * CSCI 2120 Fall 2014
 * Risk Game Class PlaceArmiesScreen
 * Authors: Hung Le, Jeanne Vu 
 * Date: 11-25-2014
 **
 * Class:	PlaceArmiesScreen - the subScreen in which the player can place armies into territories
 * Composition: contained in PlayerTurnScreen
 **/

 import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.BoxLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observer;
import java.util.Observable;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.ArrayList;

public class PlaceArmiesScreen extends JPanel implements Observer{


	private RiskGameEngine gameEngine;
     private RiskGame game;
     private ArrayList<Player> playersList;
	private GameBoard gameBoard;
	
	private ImageComponent imageComponent;


	private JPanel territoriesListPanel;
	private CardLayout terrListPaneLayout;
	private JComboBox territoriesList;


	private String selectedTerritory;
	private ComboBoxHandler boxHandler;
	
	private JButton placeArmiesButton;

	private JPanel numArmiesJP;
	private JLabel numArmiesDisplay;

	private JPanel numArmiesToPlaceJP;
	private JTextField numArmiesToPlaceJTF;
	private int numArmiesToPlace=0;

	
	public PlaceArmiesScreen(RiskGameEngine gameEngine, Images riskBoard){

		this.gameEngine = gameEngine;
		this.imageComponent = new ImageComponent(riskBoard.getImage(1100,620));
		
		setLayout( new BorderLayout());

		placeArmiesButton = new JButton("Place Armies");

		ButtonHandler buttonHandler = new ButtonHandler();
		placeArmiesButton.addActionListener( buttonHandler );

		territoriesListPanel = new JPanel(new CardLayout());
		terrListPaneLayout = (CardLayout) territoriesListPanel.getLayout();
		terrListPaneLayout.setVgap(295);
		
		numArmiesJP = new JPanel(new FlowLayout());


		numArmiesToPlaceJP = new JPanel( new FlowLayout() );
		numArmiesToPlaceJTF = new JTextField("Num of Armies...",8);
		TextFieldHandler textHandler= new TextFieldHandler();
		numArmiesToPlaceJTF.addActionListener(textHandler);
		numArmiesToPlaceJTF.setHorizontalAlignment((int)Component.CENTER_ALIGNMENT);
		numArmiesToPlaceJP.add(numArmiesToPlaceJTF);

		
		add( numArmiesJP, BorderLayout.NORTH);
		add( placeArmiesButton , BorderLayout.SOUTH);
		add( imageComponent, BorderLayout.CENTER);
		add( territoriesListPanel, BorderLayout.WEST);
		add( numArmiesToPlaceJP, BorderLayout.EAST);

		boxHandler = new ComboBoxHandler();

		MouseHandler mouseHandler = new MouseHandler();
		numArmiesToPlaceJTF.addMouseListener(mouseHandler);


	}

     public void update( Observable obs, Object obj)
	{
		this.numArmiesDisplay.setText( "Number of armies available: "+game.getCurrentPlayer().getNumArmies()+".");
	}
	
	// Handler For Mouse Click
	private class MouseHandler extends MouseAdapter
	{
	    	@Override
	    	public void mouseClicked(MouseEvent e)
	    	{
	    		numArmiesToPlaceJTF.setText("");
	    	}
	}

	// Handler for Buttons
	private class ButtonHandler implements ActionListener{
		public ButtonHandler(){

		}

		public void actionPerformed (ActionEvent event)
		{
				
			if(event.getActionCommand().equals("Place Armies") && selectedTerritory != null)
			{
				
				game.getCurrentPlayer().updateNumArmies(-numArmiesToPlace);
				game.notifyChanges();
				gameBoard.getTerritoryByName(selectedTerritory).updateNumArmies(numArmiesToPlace);
				//numArmiesToPlaceJTF.setText("Num of Armies...");
				if( game.getCurrentPlayer().getNumArmies() == 0 )
				{
				// switch to the next subscreen
					System.out.println("im here");
					game.setState("attackScreen");
				}
				
			}
			
		}
	}

	// Handler for TextFields
	private class TextFieldHandler implements ActionListener
	{
		public TextFieldHandler()
		{
		}

		public void actionPerformed (ActionEvent event)
		{
			numArmiesToPlace = Integer.parseInt(event.getActionCommand());

			// automatically set the number of armies to place to minimum
			if (numArmiesToPlace < 0) 
				numArmiesToPlace = 1;

			// automatically set the number of armies to place to maximum
			else if(numArmiesToPlace > game.getCurrentPlayer().getNumArmies())
				numArmiesToPlace = game.getCurrentPlayer().getNumArmies();
		
		}
	}

	
	// event handler for JComboBox

	private class ComboBoxHandler implements ActionListener{ 
		public ComboBoxHandler(){

		}

		public void actionPerformed (ActionEvent event){
			selectedTerritory = (String) ((JComboBox)event.getSource()).getSelectedItem();
			System.out.println(selectedTerritory+" chosen");
		}
	}

     public void initialSetup()
	{
	     if ( game == null )
	     {
	          System.out.println("Game is Null");
	          this.game = gameEngine.getGame();
	          this.gameBoard = game.getGameBoard();
		     this.playersList = game.getPlayersList();
		}

		System.out.println("innitial setup for place armies screen");

		game.calculateNumArmies();
		
          // JLabel to display the number or armies available for the current player
          numArmiesDisplay = new JLabel("Number of armies available: "+game.getCurrentPlayer().getNumArmies()+".");
          numArmiesJP.add(numArmiesDisplay);

          // JComboBox to display the list of territories available to occupy
          territoriesList = new JComboBox ( game.getCurrentPlayer().getTerritoriesMap().keySet().toArray() );
		territoriesListPanel.add(territoriesList,"territoriesList");
		terrListPaneLayout.first(territoriesListPanel);
		
		territoriesList.addActionListener( boxHandler );
	}

	public void update()
	{
		game.calculateNumArmies();
          territoriesList = updateCBItemList(territoriesList,game.getCurrentPlayer().getTerritoriesMap().keySet().toArray() );
          numArmiesToPlaceJTF.setText("Num of Armies...");
	}

	private JComboBox updateCBItemList(JComboBox comboBox, Object[] territoriesList)
	{
	     comboBox.removeAllItems();
	     for ( Object territoryName : territoriesList )
	          comboBox.addItem(territoryName);
	     return comboBox;
	}
}