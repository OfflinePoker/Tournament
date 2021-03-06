package de.hatoka.tournament.internal.app.models;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.hatoka.common.capi.app.model.MessageVO;

@XmlRootElement
public class FrameModel
{
    @XmlElementWrapper(name = "mainMenu")
    @XmlElement(name = "menuItem")
    private List<MenuItemVO> mainMenuItems = new ArrayList<>();

    @XmlElementWrapper(name = "sideMenu")
    @XmlElement(name = "menuItem")
    private List<MenuItemVO> sideNavItems = new ArrayList<>();

    @XmlAttribute
    private String title = null;

    @XmlAttribute
    private String titleKey = null;

    @XmlElement
    private String content = null;

    @XmlAttribute
    private boolean isLoggedIn = false;

    @XmlAttribute
    private URI uriHome = null;

    @XmlAttribute
    private URI uriLogin = null;

    @XmlAttribute
    private URI uriLogout = null;

    @XmlElementWrapper(name = "messages")
    @XmlElement(name = "message")
    private List<MessageVO> messages = new ArrayList<MessageVO>();

    public FrameModel()
    {
    }

    public MenuItemVO addMainMenu(String titleKey, URI uri, boolean isActive)
    {
        MenuItemVO menuItemVO = new MenuItemVO(titleKey, uri, isActive);
        getMainMenuItems().add(menuItemVO);
        return menuItemVO;
    }

    public MenuItemVO addSideMenu(String titleKey, URI uriList, Integer count, URI uriAdd, boolean isActive)
    {
        MenuItemVO menuItemVO = new MenuItemVO(titleKey, uriList, isActive, count, uriAdd);
        getSideNavItems().add(menuItemVO);
        return menuItemVO;
    }

    @XmlTransient
    public List<MenuItemVO> getMainMenuItems()
    {
        return mainMenuItems;
    }

    @XmlTransient
    public List<MenuItemVO> getSideNavItems()
    {
        return sideNavItems;
    }

    @XmlTransient
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    @XmlTransient
    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public void setMainMenuItems(List<MenuItemVO> mainMenuItems)
    {
        this.mainMenuItems = mainMenuItems;
    }

    public void setSideNavItems(List<MenuItemVO> sideNavItems)
    {
        this.sideNavItems = sideNavItems;
    }

    @XmlTransient
    public URI getUriHome()
    {
        return uriHome;
    }

    public void setUriHome(URI uriHome)
    {
        this.uriHome = uriHome;
    }

    @XmlTransient
    public String getTitleKey()
    {
        return titleKey;
    }

    public void setTitleKey(String titleKey)
    {
        this.titleKey = titleKey;
    }

    @XmlTransient
    public List<MessageVO> getMessages()
    {
        return messages;
    }

    public void setMessages(List<MessageVO> messages)
    {
        this.messages = messages;
    }

    public void addMessages(Collection<MessageVO> messages)
    {
        this.messages.addAll(messages);
    }

    public void addMessage(MessageVO message)
    {
        this.messages.add(message);
    }

    @XmlTransient
    public URI getUriLogin()
    {
        return uriLogin;
    }

    public void setUriLogin(URI uriLogin)
    {
        this.uriLogin = uriLogin;
    }

    @XmlTransient
    public URI getUriLogout()
    {
        return uriLogout;
    }

    public void setUriLogout(URI uriLogout)
    {
        this.uriLogout = uriLogout;
    }

    public boolean isLoggedIn()
    {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean isLoggedIn)
    {
        this.isLoggedIn = isLoggedIn;
    }
}
