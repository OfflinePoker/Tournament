package de.hatoka.tournament.internal.app.servlets;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.hatoka.common.capi.app.servlet.AbstractService;
import de.hatoka.tournament.capi.business.PlayerBO;
import de.hatoka.tournament.capi.business.TournamentBORepository;
import de.hatoka.tournament.capi.business.TournamentBusinessFactory;
import de.hatoka.tournament.internal.app.actions.PlayerAction;
import de.hatoka.tournament.internal.app.actions.TournamentAction;
import de.hatoka.tournament.internal.app.menu.MenuFactory;
import de.hatoka.tournament.internal.app.models.TournamentPlayerListModel;

@Path("/tournament/{id}")
public class TournamentCompetitorService extends AbstractService
{
    private static final String RESOURCE_PREFIX = "de/hatoka/tournament/internal/templates/app/";

    @PathParam("id")
    private String tournamentID;

    @Context
    private UriInfo info;

    private AccountService accountService;
    private final MenuFactory menuFactory = new MenuFactory();

    private Response redirect;

    public TournamentCompetitorService()
    {
        super(RESOURCE_PREFIX);
        accountService = new AccountService(this);
    }

    public TournamentCompetitorService(AccountService accountService)
    {
        super(RESOURCE_PREFIX);
        this.accountService = accountService;
    }

    public void setAccountService(AccountService accountService)
    {
        this.accountService = accountService;
    }

    private PlayerAction getPlayerAction()
    {
        String accountRef = accountService.getAccountRef();
        if (accountRef == null)
        {
            redirect = accountService.redirectLogin();
            return null;
        }
        return new PlayerAction(accountRef, getInstance(TournamentBusinessFactory.class));
    }

    private TournamentAction getTournamentAction()
    {
        String accountRef = accountService.getAccountRef();
        if (accountRef == null)
        {
            redirect = accountService.redirectLogin();
            return null;
        }
        TournamentBusinessFactory factory = getInstance(TournamentBusinessFactory.class);
        TournamentBORepository tournamentBORepository = factory.getTournamentBORepository(accountRef);
        return new TournamentAction(accountRef, tournamentBORepository.getTournamentByID(tournamentID), factory);
    }

    @POST
    @Path("/actionPlayerList")
    public Response actionPlayerList(@FormParam("competitorID") List<String> identifiers,
                    @FormParam("delete") String deleteButton, @FormParam("seatopen") String seatOpenButton,
                    @FormParam("sort") String sortButton, @FormParam("rebuy") String rebuyButton, @FormParam("buyin") String buyInButton)
    {
        if (isButtonPressed(sortButton))
        {
            return sortPlayers();
        }
        if (isButtonPressed(deleteButton))
        {
            return unassignPlayers(identifiers);
        }
        if (isButtonPressed(buyInButton))
        {
            return buyInPlayers(identifiers);
        }
        if (isButtonPressed(rebuyButton))
        {
            return rebuyPlayers(identifiers);
        }
        if (isButtonPressed(seatOpenButton))
        {
            return seatOpenPlayer(identifiers.get(0));
        }
        return redirectPlayers();
    }

    @POST
    @Path("/sortPlayers")
    public Response sortPlayers()
    {
        TournamentAction action = getTournamentAction();
        if (action == null)
        {
            return redirect;
        }
        runInTransaction(new Runnable()
        {
            @Override
            public void run()
            {
                action.sortPlayers();
            }
        });
        return redirectPlayers();
    }

    @POST
    @Path("/assignPlayer")
    public Response assignPlayer(@FormParam("playerID") String playerID)
    {
        TournamentAction action = getTournamentAction();
        if (action == null)
        {
            return redirect;
        }
        runInTransaction(new Runnable()
        {
            @Override
            public void run()
            {
                PlayerBO playerBO = getPlayerAction().getPlayer(playerID);
                action.register(playerBO);
            }
        });
        return redirectAddPlayer();
    }

    @POST
    @Path("/createPlayer")
    public Response createPlayer(@FormParam("name") String name)
    {
        TournamentAction action = getTournamentAction();
        if (action == null)
        {
            return redirect;
        }
        runInTransaction(new Runnable()
        {
            @Override
            public void run()
            {
                PlayerBO playerBO = getPlayerAction().createPlayer(name);
                action.register(playerBO);
            }
        });
        return redirectAddPlayer();
    }

    @GET
    @Path("/players.html")
    public Response players()
    {
        TournamentAction action = getTournamentAction();
        if (action == null)
        {
            return redirect;
        }
        final TournamentPlayerListModel model = action.getPlayerListModel(
                        getUriBuilder(TournamentListService.class, "list").build(),
                        getUriBuilder(TournamentCompetitorService.class, "players"));
        try
        {
            String content = renderStyleSheet(model, "tournament_players.xslt", getXsltProcessorParameter("tournament"));
            return Response.status(200).entity(renderFrame(content, "title.list.players")).build();
        }
        catch(IOException e)
        {
            return render(500, e);
        }
    }

    @GET
    @Path("/addPlayer.html")
    public Response addPlayer()
    {
        TournamentAction action = getTournamentAction();
        if (action == null)
        {
            return redirect;
        }
        final TournamentPlayerListModel model = action.getPlayerListModel(
                        getUriBuilder(TournamentListService.class, "list").build(),
                        getUriBuilder(TournamentCompetitorService.class, "players"));
        try
        {
            String content = renderStyleSheet(model, "tournament_player_add.xslt",
                            getXsltProcessorParameter("tournament"));
            return Response.status(200).entity(renderFrame(content, "title.list.players")).build();
        }
        catch(IOException e)
        {
            return render(500, e);
        }
    }

    @POST
    @Path("/buyInPlayers")
    public Response buyInPlayers(@FormParam("competitorID") List<String> identifiers)
    {
        TournamentAction action = getTournamentAction();
        if (action == null)
        {
            return redirect;
        }
        runInTransaction(new Runnable()
        {
            @Override
            public void run()
            {
                action.buyInPlayers(identifiers);
            }
        });
        return redirectPlayers();
    }

    @POST
    @Path("/rebuyPlayers")
    public Response rebuyPlayers(@FormParam("competitorID") List<String> identifiers)
    {
        TournamentAction action = getTournamentAction();
        if (action == null)
        {
            return redirect;
        }
        runInTransaction(new Runnable()
        {
            @Override
            public void run()
            {
                action.rebuyPlayers(identifiers);
            }
        });
        return redirectPlayers();
    }

    private Response redirectPlayers()
    {
        return Response.seeOther(getUriBuilder(TournamentCompetitorService.class, "players").build(tournamentID))
                        .build();
    }

    private Response redirectAddPlayer()
    {
        return Response.seeOther(getUriBuilder(TournamentCompetitorService.class, "addPlayer").build(tournamentID))
                        .build();
    }

    @POST
    @Path("/seatOpenPlayer")
    public Response seatOpenPlayer(@FormParam("competitorID") String identifier)
    {
        TournamentAction action = getTournamentAction();
        if (action == null)
        {
            return redirect;
        }
        runInTransaction(new Runnable()
        {
            @Override
            public void run()
            {
                action.seatOpenPlayers(identifier);
            }
        });
        return redirectPlayers();
    }

    @POST
    @Path("/unassignPlayers")
    public Response unassignPlayers(@FormParam("competitorID") List<String> identifiers)
    {
        TournamentAction action = getTournamentAction();
        if (action == null)
        {
            return redirect;
        }
        runInTransaction(new Runnable()
        {
            @Override
            public void run()
            {
                action.unassignPlayers(identifiers);
            }
        });
        return redirectPlayers();
    }

    private String renderFrame(String content, String titleKey) throws IOException
    {
        TournamentBusinessFactory factory = getInstance(TournamentBusinessFactory.class);
        TournamentBORepository tournamentBORepository = factory.getTournamentBORepository(accountService
                        .getAccountRef());
        return renderStyleSheet(menuFactory.getTournamentFrameModel(content, titleKey, getInfo(),
                        tournamentBORepository, tournamentID), "tournament_frame.xslt",
                        getXsltProcessorParameter("tournament"));
    }
}