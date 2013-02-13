package com.solovyev.games.gwttetris.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.*;

public class GameOverViewImpl implements GameOverView
{
    private Presenter presenter;
    
    private GameOverDialog gameOverDialog;
    private NameInputDialog nameInputDialog;

    @UiTemplate("GameOverViewImpl#GameOverDialog.ui.xml")
    public interface GameOverDialogUiBinder extends UiBinder<DialogBox, GameOverDialog> {}
    private static GameOverDialogUiBinder gameOverDialogUiBinder = GWT.create(GameOverDialogUiBinder.class);

    @UiTemplate("GameOverViewImpl#NameInputDialog.ui.xml")
    public interface NameInputDialogUiBinder extends UiBinder<DialogBox, NameInputDialog> {}
    private static NameInputDialogUiBinder nameInputDialogUiBinder = GWT.create(NameInputDialogUiBinder.class);

    @Override
    public void setPresenter(Presenter presenter)
    {
        this.presenter = presenter;
    }
   
    @Override
    public void showGameOverDialog()
    {
        gameOverDialog = new GameOverDialog();
        gameOverDialog.getDialogBox().center();
        gameOverDialog.getDialogBox().show();
    }
    
    @Override
    public void hideGameOverDialog()
    {
        gameOverDialog.getDialogBox().hide();
    }
    
    @Override
    public void showNameInputDialog()
    {
        nameInputDialog = new NameInputDialog();
        nameInputDialog.getDialogBox().center();
        nameInputDialog.getDialogBox().show();
    }
    
    @Override
    public void hideNameInputDialog()
    {
        nameInputDialog.getDialogBox().hide();        
    }

    public class GameOverDialog
    {
        private DialogBox dialogBox;
        
        @UiField
        public Button okButton;
        
        public GameOverDialog()
        {
            dialogBox = gameOverDialogUiBinder.createAndBindUi(this);
            
            okButton.addClickHandler(new ClickHandler()
            {
                @Override
                public void onClick(ClickEvent event)
                {
                    presenter.handleGameOverOkButton();
                }
            });
        }
        
        public DialogBox getDialogBox()
        {
            return dialogBox;    
        }
    }
    
    public class NameInputDialog
    {
        private DialogBox dialogBox;
        
        @UiField
        public Button okButton;
        
        @UiField
        public Button cancelButton;
        
        @UiField
        public TextBox nameTextBox;
       
        public NameInputDialog()
        {
            dialogBox = nameInputDialogUiBinder.createAndBindUi(this);
            
            okButton.addClickHandler(new ClickHandler()
            {
                @Override
                public void onClick(ClickEvent event)
                {
                    presenter.handleNameInputOkButton(nameTextBox.getText());
                }
            });

            cancelButton.addClickHandler(new ClickHandler()
            {
                @Override
                public void onClick(ClickEvent event)
                {
                    presenter.handleNameInputCancelButton();
                }
            });
        }
        
        public DialogBox getDialogBox()
        {
            return dialogBox;
        }
    }
}