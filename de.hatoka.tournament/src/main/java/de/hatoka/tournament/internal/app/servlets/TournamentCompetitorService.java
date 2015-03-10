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
import de.hatoka.tournament.capi.business.TournamentBORepository;
import de.hatoka.tournament.capi.business.TournamentBusinessFactory;
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

    @POST
    @Path("/actionPlayerList")
    public Response actionPlayerList(@FormParam("competitorID") List<String> identifiers,
                    @FormParam("delete") String deleteButton, @FormParam("seatopen") String seatOpenButton, @FormParam("sort") String sortButton,
                    @FormParam("rebuy") String rebuyButton, @FormParam("amount") String amount)
    {
        if (isButtonPressed(sortButton))
        {
            return sortPlayers();
        }
        if (isButtonPressed(deleteButton))
        {
            return unassignPlayers(identifiers);
        }
        if (isButtonPressed(rebuyButton))
        {
            return rebuyPlayers(identifiers, amount);
        }
        if (isButtonPressed(seatOpenButton))
        {
            return seatOpenPlayers(identifiers, amount);
        }
        return redirectPlayers();
    }

    @POST
    @Path("/sortPlayers")
    public Response sortPlayers()
    {
        String accountRef = accountService.getAccountRef();
        if (accountRef == null)
        {
            return accountService.redirectLogin();
        }
        TournamentAction action = getAction(accountRef);
        runInTransaction(new Runnable()
        {
            @Override
            public void run()
            {
                action.sortPlayers(tournamentID);
            }
        });
        return redirectPlayers();
    }

    @POST
    @Path("/assignPlayer")
    public Response assignPlayer(@FormParam("playerID") String playerID)
    {
        String accountRef = accountService.getAccountRef();
        if (accountRef == null)
        {
            return accountService.redirectLogin();
        }
        TournamentAction action = getAction(accountRef);
        runInTransaction(new Runnable()
        {
            @Override
            public void run()
            {
                action.assignPlayer(tournamentID, playerID);
            }
        });
        return redirectPlayers();
    }

    @POST
    @Path("/createPlayer")
    public Response createPlayer(@FormParam("name") String name)
    {
        String accountRef = accountService.getAccountRef();
        if (accountRef == null)
        {
            return accountService.redirectLogin();
        }
        TournamentAction action = getAction(accountRef);
        runInTransaction(new Runnable()
        {
            @Override
            public void run()
            {
                action.createPlayer(tournamentID, name);
            }
        });
        return redirectPlayers();
    }

    protected TournamentAction getAction(String accountRef)
    {
        TournamentAction tournamentAction = new TournamentAction(accountRef);
        getInjector().injectMembers(tournamentAction);
        return tournamentAction;
    }

    @GET
    @Path("/players.html")
    public Response players()
    {
        String accountRef = accountService.getAccountRef();
        if (accountRef == null)
        {
            return accountService.redirectLogin();
        }
        final TournamentPlayerListModel model = getAction(accountRef).getPlayerListModel(tournamentID,
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

    @POST
    @Path("/rebuyPlayers")
    public Response rebuyPlayers(@FormParam("competitorID") List<String> identifiers, @FormParam("rebuy") String rebuy)
    {
        String accountRef = accountService.getAccountRef();
        if (accountRef == null)
        {
            return accountService.redirectLogin();
        }
        TournamentAction action = getAction(accountRef);
        runInTransaction(new Runnable()
        {
            @Override
            public void run()
            {
                action.rebuyPlayers(tournamentID, identifiers, rebuy);
            }
        });
        return redirectPlayers();
    }

    private Response redirectPlayers()
    {
        return Response.seeOther(getUriBuilder(TournamentCompetitorService.class, "players").build(tournamentID)).build();
    }

    @POST
    @Path("/seatOpenPlayers")
    public Response seatOpenPlayers(@FormParam("competitorID") List<String> identifiers,
                    @FormParam("amount") String amount)
    {
        String accountRef = accountService.getAccountRef();
        if (accountRef == null)
        {
            return accountService.redirectLogin();
        }
        TournamentAction action = getAction(accountRef);
        runInTransaction(new Runnable()
        {
            @Override
            public void run()
            {
                action.seatOpenPlayers(tournamentID, identifiers, amount);
            }
        });
        return redirectPlayers();
    }

    @POST
    @Path("/unassignPlayers")
    public Response unassignPlayers(@FormParam("competitorID") List<String> identifiers)
    {
        String accountRef = accountService.getAccountRef();
        if (accountRef == null)
        {
            return accountService.redirectLogin();
        }
        TournamentAction action = getAction(accountRef);
        runInTransaction(new Runnable()
        {
            @Override
            public void run()
            {
                action.unassignPlayers(tournamentID, identifiers);
            }
        });
        return redirectPlayers();
    }

    private String renderFrame(String content, String titleKey) throws IOException
    {
        TournamentBusinessFactory factory = getInstance(TournamentBusinessFactory.class);
        TournamentBORepository tournamentBORepository = factory.getTournamentBORepository(accountService.getAccountRef());
        return renderStyleSheet(menuFactory.getTournamentFrameModel(content, titleKey, getInfo(), tournamentBORepository, tournamentID), "tournament_frame.xslt", getXsltProcessorParameter("tournament"));
    }
}