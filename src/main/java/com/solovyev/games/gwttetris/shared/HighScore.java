package com.solovyev.games.gwttetris.shared;

import java.io.Serializable;
import java.util.Date;

public class HighScore implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String name;
    private Integer value;
    private Date date;
    
    public HighScore(String name, Integer score, Date date)
    {
        this.name = name;
        this.value = score;
        this.date = date;
    }
    
    private HighScore()
    {        
    }

    public String getName()
    {
        return name;
    }

    public Integer getValue()
    {
        return value;
    }

    public Date getDate()
    {
        return date;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HighScore that = (HighScore) o;

        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "HighScore{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", date=" + date +
                '}';
    }
}