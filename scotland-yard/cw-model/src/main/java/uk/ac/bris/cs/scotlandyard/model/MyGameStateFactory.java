package uk.ac.bris.cs.scotlandyard.model;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;
import uk.ac.bris.cs.scotlandyard.model.Move.SingleMove;
import javax.annotation.Nonnull;
import java.util.*;


/**
 * cw-model
 * Stage 1: Complete this class
 */
public final class MyGameStateFactory implements Factory<GameState> {
	@Nonnull
	@Override
	public GameState build(
			GameSetup setup,
			Player mrX,
			ImmutableList<Player> detectives) {
		return new MyGameState(setup, ImmutableSet.of(Piece.MrX.MRX), ImmutableList.of(), mrX, detectives);
	}

	@Nonnull
	@Override
	public GameSetup getSetup() {
		return null;
	}

	@Nonnull
	@Override
	public ImmutableSet<Piece> getPlayers() {
		return null;
	}

	@Nonnull
	@Override
	public Optional<Integer> getDetectiveLocation(Piece.Detective detective) {
		return Optional.empty();
	}

	@Nonnull
	@Override
	public Optional<Board.TicketBoard> getPlayerTickets(Piece piece) {
		return Optional.empty();
	}

	@Nonnull
	@Override
	public ImmutableList<LogEntry> getMrXTravelLog() {
		return null;
	}

	@Nonnull
	@Override
	public ImmutableSet<Piece> getWinner() {
		return null;
	}

	@Nonnull
	@Override
	public ImmutableSet<Move> getAvailableMoves() {
		return null;
	}

	@Nonnull
	@Override
	public GameState advance(Move move) {
		return null;
	}


	/**
	 * @return the current game setup
	 */
	private static final class MyGameState implements GameState {
		private GameSetup setup;
		private ImmutableSet<Piece> remaining;
		private ImmutableList<LogEntry> log;
		private Player mrX;
		private List<Player> detectives;
		private ImmutableList<Player> everyone;
		private ImmutableSet<Move> moves;
		private ImmutableSet<Piece> winner;

		private MyGameState(final GameSetup setup,
							final ImmutableSet<Piece> remaining,
							final ImmutableList<LogEntry> log,
							final Player mrX,
							final List<Player> detectives) {
			this.setup = setup;
			this.remaining = remaining;
			this.log = log;
			this.mrX = mrX;
			//Add detectives and mrX to builder everyone
			everyone = new ImmutableList.Builder<Player>()
					.addAll(detectives)
					.add(mrX)
					.build();

			//check if there is more than one round to play
			if (setup.rounds.isEmpty())
				throw new IllegalArgumentException("There should be at least one round to play");
			if (setup.graph.edges().isEmpty()) throw new IllegalArgumentException();

			//Initialise winner variable
			//Check if detectives have same location
			Set<Integer> playerLocation = new HashSet<>();
			if (mrX.isDetective()) throw new IllegalArgumentException();
			for (Player player : this.detectives = detectives) {
				if (playerLocation.contains(player.location()))
					throw new IllegalArgumentException("No two detectives can have the same location");
				playerLocation.add(player.location());
				for (final var p : detectives) {
					if (p.has(ScotlandYard.Ticket.DOUBLE) || p.has(ScotlandYard.Ticket.SECRET))
						throw new IllegalArgumentException("Only mrX can have Double and Secret ticket");

				}
			}
			// For winning
			Set<Piece> winnerSet = new HashSet<>();
			if (!(AllDetectivesAreStuck())){
				winnerSet.add(mrX.piece());
			}
			//mrX is captured
			if (MrxCaptured()) {
				for (final var d : detectives) {
					winnerSet.add(d.piece());
				}
			}

			//No rounds left,so mrX has not be captured so he wins
			if ((log.size() == setup.rounds.size()) && (remaining.contains(mrX.piece()))) {
				winnerSet.add(mrX.piece());
			}
			if((winnerSet.contains(mrX.piece())) && !(mrX.piece().webColour().equals("#000"))){
				throw new IllegalArgumentException();
			}

			//
			if (AvailableMoves().isEmpty() && (!remaining.contains(mrX.piece()))) {this.remaining = ImmutableSet.of(mrX.piece()); }


			//If mrX cannot make any move and cannot escape then his set of AvailableMoves  is empty and we add detectives as winners
			if (AvailableMoves().isEmpty()) {
				if (this.remaining.contains(mrX.piece())) {
					for (final var d : detectives) {
						winnerSet.add(d.piece());
					}
				}
			}
			winner = ImmutableSet.copyOf(winnerSet);
			if (!(getAvailableMoves().isEmpty()) && !(getWinner().isEmpty())) throw new IllegalArgumentException();
			System.out.println(winnerSet);
		}


		//detectives.tickets() is an ImmutableMap so this function,iterates through the values of the keys and returns true if the keys of the map have a value larger than 0
		//So if the detectives have 0 tickets ,they are stuck and they loose
		private boolean AllDetectivesAreStuck(){
			for (final var detective : detectives){
				for(final int i : detective.tickets().values()){
					if(i>0) return true;
				}
			}
			return false;
		}
		//If mrX's location is the same as a detective then the function returns true and mrX looses because he is being caught
		private boolean MrxCaptured(){
			for (final var player : detectives){
				if ( mrX.location() == player.location()) return true;
			}
			return false;
		}

		@Nonnull
		@Override
		public GameSetup getSetup() {
			return setup;
		}
		/**
		 * @return all players in the game
		 */
		@Nonnull
		@Override
		public ImmutableSet<Piece> getPlayers() {
			//Linked bc hash sets originally return objects without a particular order
			Set<Piece> players = new LinkedHashSet<Piece>();
			players.add(mrX.piece());
			//For each detective add a piece in the Set players
			for (Player piece : detectives) {
				players.add(piece.piece());
			}
			return ImmutableSet.copyOf(players);
		}

		/**
		 * @param detective the detective
		 * @return the location of the given detective; empty if the detective is not part of the game
		 */
		@Nonnull
		@Override
		public Optional<Integer> getDetectiveLocation(Piece.Detective detective) {
			for (final var p : detectives) {
				if (p.piece() == detective) return Optional.of(p.location());
			}
			return Optional.empty();
		}
		/**
		 * @param piece the player piece
		 * @return the ticket board of the given player; empty if the player is not part of the game
		 */
		@Nonnull
		@Override
		public Optional<Board.TicketBoard> getPlayerTickets(Piece piece) {
			class PTicketBoard implements TicketBoard {
				@Override
				public int getCount(@Nonnull ScotlandYard.Ticket ticket) {
					for (final var p : everyone) {
						if ((p.has(ticket)) && ((p.piece() == piece))) {
							return p.tickets().get(ticket);
						}
					}
					return 0;
				}
			}
			//Creating a new  TicketBoard and returning an optional of the TicketBoard of the required piece
			TicketBoard newTicketBoard = new PTicketBoard();
			for (final var p : everyone) {
				if (p.piece() == piece) {
					return Optional.of(newTicketBoard);
				}
			}
			return Optional.empty();
		}
		@Nonnull
		@Override
		public ImmutableList<LogEntry> getMrXTravelLog() { return log; }

		@Nonnull
		@Override
		public ImmutableSet<Piece> getWinner() { return winner; }

		public ImmutableSet<Move> AvailableMoves() {
			Set<Move> availableMoves = new LinkedHashSet<>();
			//if remaining contains mrX then move starts by mrX,because he makes the first move,add all the possible moves he can make at the moment
			if (remaining.contains(mrX.piece())) {
				availableMoves.addAll(makeSingleMoves(setup, detectives, mrX, mrX.location()));
				if (log.size()+1< setup.rounds.size()) {
					availableMoves.addAll(makeDoubleMoves(setup, detectives, mrX, mrX.location()));
				}
				//if remaining contains detectives for each detective add all the possible moves he can make at the moment
			}else {
				for (Player p : detectives) {
					if (remaining.contains(p.piece())) {
						availableMoves.addAll(makeSingleMoves(setup, detectives, p, p.location()));
					}
				}
			}
			moves=ImmutableSet.copyOf(availableMoves);
			return moves;
		}
		@Nonnull
		@Override
		public ImmutableSet<Move> getAvailableMoves() {

			if (!(getWinner().isEmpty())){
				moves = ImmutableSet.of();
			}
			return moves;
		}

		@Nonnull
		@Override
		//Advance method which creates a new GameState that reflects the new state after a Move has been made
		public GameState advance(Move move) {
			if (!(getAvailableMoves().contains(move))) throw new IllegalArgumentException("Illegal move: " + move);
			return new MyGameState(setup, remainingFunction(move), move.visit(visitor), mrX, detectives);
		}

		//Creating a Set with the remaining players needed for MyGameState
		public ImmutableSet<Piece> remainingFunction(Move move) {
			Set<Piece> remainingPlayers = new LinkedHashSet<>();
			if (!(move.commencedBy().isDetective())) {
				for (final var p : detectives) {
					remainingPlayers.add(p.piece());
				}
			}
			if (move.commencedBy().isDetective()) {
				for (final var p : detectives) {
					if (!(move.commencedBy() == p.piece()) && (remaining.contains(p.piece()))) {
						remainingPlayers.add(p.piece());
					}
				}
			}
			if (remainingPlayers.isEmpty()) {
				remainingPlayers.add(mrX.piece());
			}
			remaining = ImmutableSet.copyOf(remainingPlayers);
			return remaining;
		}
		//Visitor pattern so that the program distinguishes the type of move, called then by the advance function
		Move.Visitor<ImmutableList<LogEntry>> visitor = new Move.Visitor<>() {
			List<LogEntry> Log = new ArrayList<>();
			@Override
			public ImmutableList<LogEntry> visit(SingleMove move) {
				//UPDATE THE LOG
				//To keep old log elements in Log
				Log.addAll(log);
				if((move.commencedBy().isMrX())) {
					//Setup.rounds is an ImmutableList of booleans which contains 24 booleans ,if the ith position is true then it is a reveal round if it is false then hidden
					if (setup.rounds.get(Log.size())) {
						Log.add(LogEntry.reveal(move.ticket, move.destination));
					} else {
						Log.add(LogEntry.hidden(move.ticket));
					}
					//Creates a new Player mrX with the correct number of tickets and to the correct destination
					mrX = mrX.at(move.destination);
					mrX = mrX.use(move.ticket);
					log = ImmutableList.copyOf(Log);
				}
				//UPDATE DETECTIVE TICKETS AND LOCATION
				List<Player> Players = new ArrayList<>();
				if(move.commencedBy().isDetective()) {
					for (Player p : detectives) {
						if ((move.commencedBy() == p.piece()) && (!(remaining.contains(p.piece())))) {
							p=p.use(move.ticket);
							//gives the used ticket of player to mrX and returns a new player mrX with the correct number of tickets
							mrX = mrX.give(move.ticket);
							p = p.at(move.destination);
							//Creates a new Player with the correct number of tickets and to the correct destination
						}
						Players.add(p);
					}
					detectives= new ImmutableList.Builder<Player>()
							.addAll(Players)
							.build();
				}
				/*for(final var v:Log){
					System.out.println(setup.rounds);
					System.out.println(mrX);
					System.out.println("LogEntry  :" + Log);
					System.out.println("Location  :" + v.location());
					System.out.println("Ticket  :" + v.ticket());
				}*/
				return log;
			}

			@Override
			public ImmutableList<LogEntry> visit(Move.DoubleMove move) {
				Log.addAll(log);
				if(move.commencedBy().isMrX()) {
					if (setup.rounds.get(Log.size()) && (setup.rounds.get(Log.size() + 1))) {
						Log.add(LogEntry.reveal(move.ticket1, move.destination1));
						Log.add(LogEntry.reveal(move.ticket2, move.destination2));

					} else if ((setup.rounds.get(Log.size())) && (!(setup.rounds.get(Log.size() + 1)))) {
						Log.add(LogEntry.reveal(move.ticket1, move.destination1));
						Log.add(LogEntry.hidden(move.ticket2));

					} else if (!((setup.rounds.get(Log.size())) && (setup.rounds.get(Log.size() + 1)))) {
						Log.add(LogEntry.hidden(move.ticket1));
						Log.add(LogEntry.reveal(move.ticket2, move.destination2));

					} else if ((!(setup.rounds.get(Log.size()))) && (!(setup.rounds.get(Log.size() + 1)))) {
						Log.add(LogEntry.hidden(move.ticket1));
						Log.add((LogEntry.hidden(move.ticket2)));
					}
					//UPDATE MRX TICKETS AND LOCATION
					mrX= mrX.use(ScotlandYard.Ticket.DOUBLE);
					mrX=mrX.use(move.ticket1);
					mrX=mrX.use(move.ticket2);
					mrX=mrX.at(move.destination1);
					mrX=mrX.at(move.destination2);
					log= ImmutableList.copyOf(Log);
				}
					/*for(final var v:log){
						//System.out.println(mrX);
						System.out.println(log);
						System.out.println("Location  " + v.location());
						System.out.println("Ticket  " + v.ticket());
					}*/

				return log;
			}
		};
	}
	/**
	 * Represents an on-going ScotlandYard game where moves by each player advances the game.
	 */

	private static ImmutableSet<SingleMove> makeSingleMoves(GameSetup setup, List<Player> detectives, Player player, int source) {
		final var singleMoves = new ArrayList<SingleMove>();
		for (int destination : setup.graph.adjacentNodes(source)) {
			var occupied = false;
			// TODO: find out if destination is occupied by a detective
			for (final var p : detectives) {
				if ((p.location()) == destination) {
					occupied = true;
					break;
				}
			}
			if (occupied) continue;
			for (ScotlandYard.Transport t : Objects.requireNonNull(setup.graph.edgeValueOrDefault(source, destination, ImmutableSet.of()))) {
				if (player.has(t.requiredTicket())) {
					singleMoves.add(new SingleMove(player.piece(), source, t.requiredTicket(), destination));
				}
			}
			// TODO: add moves to the destination via a Secret ticket if there are any left with the player
			if (player.has(ScotlandYard.Ticket.SECRET)) {
				singleMoves.add(new SingleMove(player.piece(), player.location(), ScotlandYard.Ticket.SECRET, destination));
			}
		}
		return ImmutableSet.copyOf(singleMoves);
	}

	private static ImmutableSet<Move.DoubleMove> makeDoubleMoves(GameSetup setup, List<Player> detectives, Player player, int source) {
		final var doubleMoves = new ArrayList<Move.DoubleMove>();
		if (player.has(ScotlandYard.Ticket.DOUBLE)) {
			for (final var move : makeSingleMoves(setup, detectives, player, source)) {
				makeSingleMoves(setup, detectives, player, move.destination);
				for (final var value : makeSingleMoves(setup, detectives, player, move.destination)) {
					makeSingleMoves(setup, detectives, player, value.destination);
					if ((move.ticket != value.ticket) || (player.hasAtLeast(move.ticket, 2))) {
						doubleMoves.add(new Move.DoubleMove(player.piece(), source, move.ticket, move.destination, value.ticket, value.destination));
					}
					if (player.has(ScotlandYard.Ticket.SECRET)) {
						doubleMoves.add(new Move.DoubleMove(player.piece(), source, move.ticket, move.destination, value.ticket, value.destination));

					}
				}
			}
		}
		return ImmutableSet.copyOf(doubleMoves);
	}
}
