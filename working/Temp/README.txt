To run the programs in this bundle, download and install Racket v6.12:

    http://www.racket-lang.org/

* To play a game of tsuro, run this script:

   ./play-tsuro

  Pass -h on the command-line to see various options. Note that
  this GUI will not win any awards and makes essentially no
  attempt to explain the rules of Tsuro to you. See this webpage
  for a link to the rules:

    https://www.calliopegames.com/read/45/tsuro

* To test your play-a-turn function, run this script:

   ./test-play-a-turn play-a-turn

  where `play-a-turn` is a program that runs your play-a-turn
  function, as described in Assignment 8.

  Pass -h on the command-line to see various options.

* To view a board graphically, run this script:

  ./visualize

  Pass -h on the command-line to see what it can show you.

* To play a tournament (possibly of your player against itself), run
  this script:

  ./tournament

  It will open a window. Connect multiple players to the server
  and click the button in the window. It will show you the results
  of each game, as win percentages.
