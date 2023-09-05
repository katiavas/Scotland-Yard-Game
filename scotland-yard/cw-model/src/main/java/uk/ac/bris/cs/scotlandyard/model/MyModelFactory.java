package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;

import java.util.*;

/**
 * cw-model
 * Stage 2: Complete this class
 */
public final class MyModelFactory implements Factory<Model> {
	MyGameStateFactory games = new MyGameStateFactory();


	@Nonnull @Override public Model build(GameSetup setup,
	                                      Player mrX,
	                                      ImmutableList<Player> detectives) {

		return new Model() {
			List<Observer> observers = new ArrayList<>();
			Board.GameState state = games.build( setup, mrX, detectives);

			@Nonnull
			@Override
			public Board getCurrentBoard() {
				return state;
			}

            //ADD THE observer OBJECT TO observers
			@Override
			public void registerObserver(@Nonnull Observer observer) {
				if (observer == null) throw new NullPointerException("Registered null observer");
		        if(observers.contains(observer)){
		        	throw new IllegalArgumentException("Same observer twice");
				}
		        observers.add(observer);
			}

			@Override
			public void unregisterObserver(@Nonnull Observer observer) {
				if (observer == null) throw new NullPointerException("Unregistered null observer");
				if(!(observers.contains(observer))){
					throw new IllegalArgumentException("Cannot remove observer that doesn't exist");
				}
				observers.remove(observer);
			}

			@Nonnull
			@Override
			public ImmutableSet<Observer> getObservers() {
				ImmutableSet<Observer> observerSet ;
				observerSet = ImmutableSet.copyOf(observers);
				return observerSet;
			}

			@Override
			public void chooseMove(@Nonnull Move move) {
				state = state.advance(move);
				var event = state.getWinner().isEmpty() ? Observer.Event.MOVE_MADE : Observer.Event.GAME_OVER;
				for (Observer o : observers) o.onModelChanged(state, event);
			}
		};
		// TODO

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
	public Board.GameState advance(Move move) {
		return null;
	}
}
