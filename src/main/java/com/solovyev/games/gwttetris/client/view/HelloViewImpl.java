package com.solovyev.games.gwttetris.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class HelloViewImpl implements HelloView
{
    private Presenter presenter;
    private Widget widget;
    private static HelloViewUiBinder uiBinder = GWT.create(HelloViewUiBinder.class);
    
    @UiField
    public Label helloLabel; 
    
    @UiField
    public Button helloButton;
    
    @UiField
    public Button fireHelloButton;

    public HelloViewImpl()
    {
        widget = uiBinder.createAndBindUi(this);

        helloButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                presenter.onHelloButtonClicked(event);
            }
        });

        fireHelloButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                presenter.onRestartButtonClicked(event);
            }
        });
    }
    
    @Override
    public void setPresenter(Presenter presenter)
    {
        this.presenter = presenter;
    }
    
    @Override
    public void hello(String message)
    {
        helloLabel.setText(message);
    }
    
    @Override
    public Widget asWidget()
    {
        return widget;
    }
    
    public interface HelloViewUiBinder extends UiBinder<Widget,HelloViewImpl> {}
}
