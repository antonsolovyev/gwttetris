package com.solovyev.games.gwttetris.client.dialog;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TextBox;
import com.solovyev.games.gwttetris.client.event.ShowHighScoresEvent;
import com.solovyev.games.gwttetris.client.service.HighScoreServiceAsync;
import com.solovyev.games.gwttetris.shared.HighScore;
import com.solovyev.games.tetris.TetrisEngine;


public class GameOverDialog implements Dialog
{
    private static NotifyDialogUiBinder notifyDialogUiBinder = GWT.create(NotifyDialogUiBinder.class);
    private static NameInputDialogUiBinder nameInputDialogUiBinder = GWT.create(NameInputDialogUiBinder.class);
    private HandlerManager eventBus;
    private TetrisEngine tetrisEngine;
    private HighScoreServiceAsync highScoreService;

    private NotifyDialog notifyDialog;
    private NameInputDialog nameInputDialog;

    public GameOverDialog(HandlerManager eventBus, TetrisEngine tetrisEngine, HighScoreServiceAsync highScoreService)
    {
        this.eventBus = eventBus;
        this.tetrisEngine = tetrisEngine;
        this.highScoreService = highScoreService;
    }

    @Override
    public void display(HasWidgets container)
    {
        showNotifyDialog();
    }

    private void showNotifyDialog()
    {
        notifyDialog = new NotifyDialog();
        notifyDialog.getDialogBox().center();
        notifyDialog.getDialogBox().show();
    }

    private void showNameInputDialog()
    {
        nameInputDialog = new NameInputDialog();
        nameInputDialog.getDialogBox().center();
        nameInputDialog.getDialogBox().show();
    }

    private void saveHighScore(String name)
    {
        highScoreService.saveHighScore(new HighScore(name, tetrisEngine.getScore(), new Date()),
            new AsyncCallback<Void>()
            {
                @Override
                public void onFailure(Throwable caught)
                {
                    throw new RuntimeException("Problem saving high score", caught);
                }

                @Override
                public void onSuccess(Void v)
                {
                    eventBus.fireEvent(new ShowHighScoresEvent());
                }
            });
    }

    private void checkHighScore()
    {
        highScoreService.isHighScore(tetrisEngine.getScore(), new AsyncCallback<Boolean>()
            {
                @Override
                public void onFailure(Throwable caught)
                {
                    throw new RuntimeException("Unable to check whether the result is a high score", caught);
                }

                @Override
                public void onSuccess(Boolean result)
                {
                    if (result)
                    {
                        showNameInputDialog();
                    }
                }
            });
    }

    @UiTemplate("GameOverDialog#NotifyDialog.ui.xml")
    public interface NotifyDialogUiBinder extends UiBinder<DialogBox, NotifyDialog>
    {
    }

    @UiTemplate("GameOverDialog#NameInputDialog.ui.xml")
    public interface NameInputDialogUiBinder extends UiBinder<DialogBox, NameInputDialog>
    {
    }

    public class NotifyDialog
    {
        private DialogBox dialogBox;

        @UiField
        public Button okButton;

        public NotifyDialog()
        {
            dialogBox = notifyDialogUiBinder.createAndBindUi(this);

            Scheduler.get().scheduleDeferred(new Command()
                {
                    public void execute()
                    {
                        okButton.setFocus(true);
                    }
                });
        }

        @UiHandler("okButton")
        public void okButtonHandler(ClickEvent clickEvent)
        {
            dialogBox.hide();

            checkHighScore();
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

            Scheduler.get().scheduleDeferred(new Command()
                {
                    public void execute()
                    {
                        nameTextBox.setFocus(true);
                    }
                });
        }

        @UiHandler("okButton")
        public void okButtonHandler(ClickEvent clickEvent)
        {
            dialogBox.hide();

            saveHighScore(nameTextBox.getText());
        }

        @UiHandler("cancelButton")
        public void cancelButtonHandler(ClickEvent clickEvent)
        {
            dialogBox.hide();
        }

        public DialogBox getDialogBox()
        {
            return dialogBox;
        }
    }
}
