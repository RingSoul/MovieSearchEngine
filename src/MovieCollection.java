import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class MovieCollection
{
    private ArrayList<Movie> movies;
    private Scanner scanner;
    private ArrayList<String> casts;
    private ArrayList<String> genres;
    private ArrayList<Movie> top50Rate;
    private ArrayList<Movie> top50Revenue;

    public MovieCollection(String fileName)
    {
        importMovieList(fileName);
        scanner = new Scanner(System.in);

        // initializing casts
        casts = new ArrayList<String>();
        for (int i = 0; i < movies.size(); i++)
        {
            Movie current = movies.get(i);
            String names = current.getCast();
            for (int x = names.indexOf("|"); x != -1; x = names.indexOf("|"))
            {
                String name = names.substring(0, x);
                casts.add(name);
                names = names.substring(x+1);
            }
            casts.add(names); // one is left out after checking all delimiters
        }
        // remove duplicates from casts
        for (int i = 0; i < casts.size(); i++)
        {
            String currentCast = casts.get(i);
            for (int x = i + 1; x < casts.size(); x++)
            {
                String comparison = casts.get(x);
                if (comparison.equals(currentCast))
                {
                    casts.remove(x);
                    x--;
                }
            }
        }
        sortResultsString(casts); // sort in advance

        // for genres
        genres = new ArrayList<String>();
        // simpler way of splitting String
        for (int i = 0; i < movies.size(); i++)
        {
            Movie movie = movies.get(i);
            String[] g = movie.getGenres().split("\\|");
            for (String x : g)
            {
                genres.add(x);
            }
        }
        // remove duplicates from genres list
        for (int i = 0; i < genres.size(); i++)
        {
            String currentGenre = genres.get(i);
            for (int x = i + 1; x < genres.size(); x++)
            {
                String comparison = genres.get(x);
                if (comparison.equals(currentGenre))
                {
                    genres.remove(x);
                    x--;
                }
            }
        }
        sortResultsString(genres); // sort in advance

        // for top 50 movies
        top50Rate = new ArrayList<Movie>();
        Movie highestRated = movies.get(0);
        Movie lowestRated = movies.get(0);
        top50Rate.add(highestRated);
        for (int i = 1; i < movies.size(); i++)
        {
            Movie current = movies.get(i);
            if (top50Rate.size() < 50)
            {
                top50Rate.add(current);
            }
            else // list full after first 50 iterations, need to delete
            {
                // always sorted already, so delete last index when necessary
                if (current.getUserRating() > lowestRated.getUserRating())
                {
                    top50Rate.add(0, current); // added in front to prevent being deleted
                    top50Rate.remove(top50Rate.size() - 1);
                }
            }
            sortRatingHighToLow(top50Rate);
            lowestRated = top50Rate.get(top50Rate.size() - 1); // update (sorted, so last = smallest)
            highestRated = top50Rate.get(0); // update (sorted, so first = biggest)
        }

        // for top 50 revenue
        top50Revenue = new ArrayList<Movie>();
        // same process, different variable names and different sort method
        Movie highestRevenueMovie = movies.get(0);
        Movie lowestRevenueMovie = movies.get(0);
        top50Revenue.add(highestRevenueMovie);
        for (int i = 1; i < movies.size(); i++)
        {
            Movie current = movies.get(i);
            if (top50Revenue.size() < 50)
            {
                top50Revenue.add(current);
            }
            else // list full after first 50 iterations, need to delete
            {
                // always sorted already, so delete last index when necessary
                if (current.getRevenue() > lowestRevenueMovie.getRevenue())
                {
                    top50Revenue.add(0, current); // added in front to prevent being deleted
                    top50Revenue.remove(top50Revenue.size() - 1);
                }
            }
            sortRevenueHighToLow(top50Revenue);
            lowestRevenueMovie = top50Revenue.get(top50Revenue.size() - 1); // update (sorted, so last = smallest)
            highestRevenueMovie = top50Revenue.get(0); // update (sorted, so first = biggest)
        }
    }

    public ArrayList<Movie> getMovies()
    {
        return movies;
    }

    public void sortRatingHighToLow(ArrayList<Movie> movies)
    {
        /* attempt of reversed selection sort */
        for (int i = 0; i < movies.size(); i++)
        {
            int maxIndex = i;
            for (int j = i + 1; j < movies.size(); j++)
            {
                if (movies.get(maxIndex).getUserRating() < movies.get(j).getUserRating())
                {
                    maxIndex = j;
                }
            }
            // swap minIndex and i
            Movie temp = movies.get(i);
            movies.set(i, movies.get(maxIndex));
            movies.set(maxIndex, temp);
        }
    }

    public void sortRevenueHighToLow(ArrayList<Movie> movies)
    {
        /* attempt of reversed selection sort */
        for (int i = 0; i < movies.size(); i++)
        {
            int maxIndex = i;
            for (int j = i + 1; j < movies.size(); j++)
            {
                if (movies.get(maxIndex).getRevenue() < movies.get(j).getRevenue())
                {
                    maxIndex = j;
                }
            }
            // swap minIndex and i
            Movie temp = movies.get(i);
            movies.set(i, movies.get(maxIndex));
            movies.set(maxIndex, temp);
        }
    }

    public void menu()
    {
        String menuOption = "";

        System.out.println("Welcome to the movie collection!");
        System.out.println("Total: " + movies.size() + " movies");

        while (!menuOption.equals("q"))
        {
            System.out.println("------------ Main Menu ----------");
            System.out.println("- search (t)itles");
            System.out.println("- search (k)eywords");
            System.out.println("- search (c)ast");
            System.out.println("- see all movies of a (g)enre");
            System.out.println("- list top 50 (r)ated movies");
            System.out.println("- list top 50 (h)igest revenue movies");
            System.out.println("- (q)uit");
            System.out.print("Enter choice: ");
            menuOption = scanner.nextLine();

            if (!menuOption.equals("q"))
            {
                processOption(menuOption);
            }
        }
    }

    private void processOption(String option)
    {
        if (option.equals("t"))
        {
            searchTitles();
        }
        else if (option.equals("c"))
        {
            searchCast();
        }
        else if (option.equals("k"))
        {
            searchKeywords();
        }
        else if (option.equals("g"))
        {
            listGenres();
        }
        else if (option.equals("r"))
        {
            listHighestRated();
        }
        else if (option.equals("h"))
        {
            listHighestRevenue();
        }
        else
        {
            System.out.println("Invalid choice!");
        }
    }

    private void searchTitles()
    {
        System.out.print("Enter a title search term: ");
        String searchTerm = scanner.nextLine();

        // prevent case sensitivity
        searchTerm = searchTerm.toLowerCase();

        // arraylist to hold search results
        ArrayList<Movie> results = new ArrayList<Movie>();

        // search through ALL movies in collection
        for (int i = 0; i < movies.size(); i++)
        {
            String movieTitle = movies.get(i).getTitle();
            movieTitle = movieTitle.toLowerCase();

            if (movieTitle.indexOf(searchTerm) != -1)
            {
                //add the Movie object to the results list
                results.add(movies.get(i));
            }
        }

        // sort the results by title
        sortResultsMovie(results);

        // now, display them all to the user
        for (int i = 0; i < results.size(); i++)
        {
            String title = results.get(i).getTitle();

            // this will print index 0 as choice 1 in the results list; better for user!
            int choiceNum = i + 1;

            System.out.println("" + choiceNum + ". " + title);
        }

        System.out.println("Which movie would you like to learn more about?");
        System.out.print("Enter number: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        Movie selectedMovie = results.get(choice - 1);

        displayMovieInfo(selectedMovie);

        System.out.println("\n ** Press Enter to Return to Main Menu **");
        scanner.nextLine();
    }

    private void sortResultsMovie(ArrayList<Movie> listToSort)
    {
        for (int j = 1; j < listToSort.size(); j++)
        {
            Movie temp = listToSort.get(j);
            String tempTitle = temp.getTitle();

            int possibleIndex = j;
            while (possibleIndex > 0 && tempTitle.compareTo(listToSort.get(possibleIndex - 1).getTitle()) < 0)
            {
                listToSort.set(possibleIndex, listToSort.get(possibleIndex - 1));
                possibleIndex--;
            }
            listToSort.set(possibleIndex, temp);
        }
    }

    private void sortResultsString(ArrayList<String> listToSort)
    {
        for (int j = 1; j < listToSort.size(); j++)
        {
            String cast = listToSort.get(j);

            int possibleIndex = j;
            while (possibleIndex > 0 && cast.compareTo(listToSort.get(possibleIndex - 1)) < 0)
            {
                listToSort.set(possibleIndex, listToSort.get(possibleIndex - 1));
                possibleIndex--;
            }
            listToSort.set(possibleIndex, cast);
        }
    }

    private void displayMovieInfo(Movie movie)
    {
        System.out.println();
        System.out.println("Title: " + movie.getTitle());
        System.out.println("Tagline: " + movie.getTagline());
        System.out.println("Runtime: " + movie.getRuntime() + " minutes");
        System.out.println("Year: " + movie.getYear());
        System.out.println("Directed by: " + movie.getDirector());
        System.out.println("Cast: " + movie.getCast());
        System.out.println("Overview: " + movie.getOverview());
        System.out.println("User rating: " + movie.getUserRating());
        System.out.println("Box office revenue: " + movie.getRevenue());
    }

    private void searchCast()
    {
        ArrayList<String> relevant = new ArrayList<String>();
        System.out.print("Enter a cast's name to search: ");
        String search = scanner.nextLine();
        search = search.toLowerCase();
        for (int i = 0; i < casts.size(); i++)
        {
            String current = casts.get(i);
            String currentLow = current.toLowerCase();
            if (currentLow.indexOf(search) != -1)
            {
                relevant.add(current);
            }
        }
        // show actor list
        for (int i = 0; i < relevant.size(); i++)
        {
            int show = i + 1;
            System.out.println(show + ". " + relevant.get(i));
        }
        System.out.print("Which actor would you like to learn more about? \nEnter number: ");
        int num = scanner.nextInt();
        num -= 1;
        String selected = relevant.get(num);
        System.out.println(selected + " had participated in the following movies...");
        // get movie lists
        ArrayList<Movie> in = new ArrayList<Movie>();
        for (int i = 0; i < movies.size(); i++)
        {
            Movie current = movies.get(i);
            if (current.getCast().indexOf(selected) != -1)
            {
                in.add(current);
            }
        }
        sortResultsMovie(in);
        // show movie list
        for (int i = 0; i < in.size(); i++)
        {
            int show = i + 1;
            System.out.println(show + ". " + in.get(i).getTitle());
        }
        System.out.print("Which movie do you want to learn more about? \nEnter the number: ");
        int index = scanner.nextInt();
        index--;
        displayMovieInfo(in.get(index));
        System.out.println("\n ** Press Enter to Return to Main Menu **");
        scanner.nextLine();
    }

    private void searchKeywords()
    {
        System.out.print("Enter a keyword to search: ");
        String searchTerm = scanner.nextLine();

        // prevent case sensitivity
        searchTerm = searchTerm.toLowerCase();

        // arraylist to hold search results
        ArrayList<Movie> results = new ArrayList<Movie>();

        // search through ALL movies in collection
        for (int i = 0; i < movies.size(); i++)
        {
            String movieKeyWord = movies.get(i).getKeywords();
            movieKeyWord = movieKeyWord.toLowerCase();

            if (movieKeyWord.indexOf(searchTerm) != -1)
            {
                //add the Movie object to the results list
                results.add(movies.get(i));
            }
        }

        // sort the results by title
        sortResultsMovie(results);

        // now, display them all to the user
        for (int i = 0; i < results.size(); i++)
        {
            String title = results.get(i).getTitle();

            // this will print index 0 as choice 1 in the results list; better for user!
            int choiceNum = i + 1;

            System.out.println("" + choiceNum + ". " + title);
        }

        System.out.println("Which movie would you like to learn more about?");
        System.out.print("Enter number: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        Movie selectedMovie = results.get(choice - 1);

        displayMovieInfo(selectedMovie);

        System.out.println("\n ** Press Enter to Return to Main Menu **");
        scanner.nextLine();
    }

    private void listGenres()
    {
        // display
        for (int i = 0; i < genres.size(); i++)
        {
            int num = i + 1;
            System.out.println(num + ". " + genres.get(i));
        }
        System.out.print("Which genre interests you? \nEnter the number: ");
        int answer = scanner.nextInt();
        answer--;
        String selected = genres.get(answer);
        ArrayList<Movie> relevant = new ArrayList<Movie>();
        for (int i = 0; i < movies.size(); i++)
        {
            Movie current = movies.get(i);
            if (current.getGenres().indexOf(selected) != -1)
            {
                relevant.add(current);
            }
        }
        sortResultsMovie(relevant);
        System.out.println(selected + " genre has the following movies...");
        for (int i = 0; i < relevant.size(); i++)
        {
            System.out.println(i+1 + ". " + relevant.get(i).getTitle());
        }
        System.out.print("Which movie are you interested? \nEnter a number: ");
        int choice = scanner.nextInt();
        choice--;
        displayMovieInfo(relevant.get(choice));
        System.out.println("\n ** Press Enter to Return to Main Menu **");
        scanner.nextLine();
    }

    private void listHighestRated()
    {
        System.out.println("Top 50 Rated Movies:");
        // display
        int num = 1;
        for (int i = 0; i < top50Rate.size(); i++)
        {
            System.out.println(num + ". " + top50Rate.get(i).getTitle() + " ----- " + top50Rate.get(i).getUserRating());
            num++;
        }
        System.out.print("Which movie are you interested in? \nEnter a number: ");
        int answer = scanner.nextInt();
        answer--;
        Movie selected = top50Rate.get(answer);
        displayMovieInfo(selected);
        System.out.println("\n ** Press Enter to Return to Main Menu **");
        scanner.nextLine();
    }

    private void listHighestRevenue()
    {
        // similar to top50Rate, just different list and different accessor method
        System.out.println("Top 50 Highest Revenue Movies:");
        // display
        int num = 1;
        for (int i = 0; i < top50Revenue.size(); i++)
        {
            System.out.println(num + ". " + top50Revenue.get(i).getTitle() + " ----- " + top50Revenue.get(i).getRevenue());
            num++;
        }
        System.out.print("Which movie are you interested in? \nEnter a number: ");
        int answer = scanner.nextInt();
        answer--;
        Movie selected = top50Revenue.get(answer);
        displayMovieInfo(selected);
        System.out.println("\n ** Press Enter to Return to Main Menu **");
        scanner.nextLine();
    }

    private void importMovieList(String fileName)
    {
        try
        {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();

            movies = new ArrayList<Movie>();

            while ((line = bufferedReader.readLine()) != null)
            {
                String[] movieFromCSV = line.split(",");

                String title = movieFromCSV[0];
                String cast = movieFromCSV[1];
                String director = movieFromCSV[2];
                String tagline = movieFromCSV[3];
                String keywords = movieFromCSV[4];
                String overview = movieFromCSV[5];
                int runtime = Integer.parseInt(movieFromCSV[6]);
                String genres = movieFromCSV[7];
                double userRating = Double.parseDouble(movieFromCSV[8]);
                int year = Integer.parseInt(movieFromCSV[9]);
                int revenue = Integer.parseInt(movieFromCSV[10]);

                Movie nextMovie = new Movie(title, cast, director, tagline, keywords, overview, runtime, genres, userRating, year, revenue);
                movies.add(nextMovie);
            }
            bufferedReader.close();
        }
        catch(IOException exception)
        {
            // Print out the exception that occurred
            System.out.println("Unable to access " + exception.getMessage());
        }
    }
}