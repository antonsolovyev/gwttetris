package com.solovyev.games.gwttetris.client.dialog;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.solovyev.games.gwttetris.client.service.HighScoreServiceAsync;
import com.solovyev.games.gwttetris.shared.HighScore;


public class HighScoreDialog implements Dialog
{
    private static final Logger logger = Logger.getLogger(HighScoreDialog.class.getName());

    private static HighScoreDialogUiBinder highScoreViewImplUiBinder = GWT.create(HighScoreDialogUiBinder.class);
    private HandlerManager handlerManager;
    private HighScoreServiceAsync highScoreService;
    private PopupPanel popupPanel;

    @UiField
    public FlexTable highScoreTable;

    @UiField
    public Button okButton;

    public HighScoreDialog(HandlerManager eventBus, HighScoreServiceAsync highScoreService)
    {
        this.handlerManager = eventBus;
        this.highScoreService = highScoreService;

        popupPanel = highScoreViewImplUiBinder.createAndBindUi(this);

        highScoreTable.setText(0, 0, "Position");
        highScoreTable.setText(0, 1, "Name");
        highScoreTable.setText(0, 2, "Score");
        highScoreTable.setText(0, 3, "Date");
    }

    @UiHandler("okButton")
    public void okButtonHandler(ClickEvent clickEvent)
    {
        popupPanel.hide();
    }

    @Override
    public void display(HasWidgets container)
    {
        populateShowHighScores();
    }

    private void populateShowHighScores()
    {
        highScoreService.getHighScores(new AsyncCallback<List<HighScore>>()
            {
                @Override
                public void onFailure(Throwable caught)
                {
                    throw new RuntimeException("Unable to get high scores", caught);
                }

                @Override
                public void onSuccess(List<HighScore> result)
                {
                    logger.log(Level.FINE, "res: " + result);

                    HighScoreDialog.this.setHighScores(result);

                    show();
                }
            });
    }

    private void setHighScores(List<HighScore> highScores)
    {
        for (int i = 0; i < highScores.size(); i++)
        {
            highScoreTable.setText(i + 1, 0, String.valueOf(i + 1));
            highScoreTable.setText(i + 1, 1, highScores.get(i).getName());
            highScoreTable.setText(i + 1, 2, String.valueOf(highScores.get(i).getScore()));
            highScoreTable.setText(i + 1, 3, String.valueOf(highScores.get(i).getDate()));
        }
    }

    private void show()
    {
        popupPanel.show();
        popupPanel.center();
        okButton.setFocus(true);
    }

    public interface HighScoreDialogUiBinder extends UiBinder<PopupPanel, HighScoreDialog>
    {
    }
}
