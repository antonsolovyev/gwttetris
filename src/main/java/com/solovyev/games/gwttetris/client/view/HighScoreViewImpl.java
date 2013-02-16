package com.solovyev.games.gwttetris.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.solovyev.games.gwttetris.shared.HighScore;

import java.util.List;

public class HighScoreViewImpl implements HighScoreView
{
    private Presenter presenter;
    private PopupPanel popupPanel;
    
    public interface HighScoreViewImplUiBinder extends UiBinder<PopupPanel, HighScoreViewImpl> {}
    public static HighScoreViewImplUiBinder highScoreViewImplUiBinder = GWT.create(HighScoreViewImplUiBinder.class);
    
    @UiField
    public FlexTable highScoreTable;
    
    @UiField
    public Button okButton;
    
    public HighScoreViewImpl()
    {
        popupPanel = highScoreViewImplUiBinder.createAndBindUi(this);
        popupPanel.center();
        popupPanel.show();
        
        highScoreTable.setText(0, 0, "Name");
        highScoreTable.setText(0, 1, "Score");
        highScoreTable.setText(0, 2, "Date");

        okButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                popupPanel.hide();
            }
        });
    }
    
    @Override
    public void setPresenter(Presenter presenter)
    {
        this.presenter = presenter;
    }
    
    @Override
    public void setHighScores(List<HighScore> highScores)
    {
        for(int i = 0; i < highScores.size(); i++)
        {
            highScoreTable.setText(i + 1, 0, highScores.get(i).getName());
            highScoreTable.setText(i + 1, 1, String.valueOf(highScores.get(i).getScore()));
            highScoreTable.setText(i + 1, 2, String.valueOf(highScores.get(i).getDate()));
        }
    }

    @Override
    public Widget asWidget()
    {
        return popupPanel;
    }
    
    @Override
    public void show()
    {
        popupPanel.show();
    }
}
