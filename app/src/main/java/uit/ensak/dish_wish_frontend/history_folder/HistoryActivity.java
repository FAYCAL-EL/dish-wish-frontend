package uit.ensak.dish_wish_frontend.history_folder;

import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import uit.ensak.dish_wish_frontend.Models.Command;
import uit.ensak.dish_wish_frontend.R;
import uit.ensak.dish_wish_frontend.history_folder.CommandAdapter;

public class HistoryActivity extends AppCompatActivity {

    private GridView gridViewDishes;
    private Button btnOrdered  , btnInProgress, btnPrepared;
    private List<Command> allCommands;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        gridViewDishes = findViewById(R.id.gridViewDishes);
        btnOrdered = findViewById(R.id.btnOrdered);
        btnInProgress = findViewById(R.id.btnInProgress);
        btnPrepared = findViewById(R.id.btnPrepared);

        allCommands = generateTestData(); // to be replaced with actual data..

        btnOrdered.setOnClickListener(v -> filterCommands("ordered"));
        btnInProgress.setOnClickListener(v -> filterCommands("in_progress"));
        btnPrepared.setOnClickListener(v -> filterCommands("prepared"));

        // Initialize the list view with all commands
        updateGridView(allCommands);
    }

    private List<Command> generateTestData() {
        // Generate some dummy data for testing
        List<Command> testData = new ArrayList<>();
        testData.add(new Command(1L,"Title 1", "Dish 1", "Serving 1", "Address 1", "Deadline 1",
                "$10.99", "Ordered", null, null));
        testData.add(new Command(2L,"Title 2", "Dish 2", "Serving 2", "Address 2", "Deadline 2",
                "$12.99", "In Progress", null, null));
        testData.add(new Command(3L,"Title 3", "Dish 3", "Serving 3", "Address 3", "Deadline 3",
                "$8.99", "Prepared", null, null));
        testData.add(new Command(4L,"Title 4", "Dish 4", "Serving 4", "Address 4", "Deadline 4",
                "$13.99", "Ordered", null, null));
        testData.add(new Command(5L,"Title 5", "Dish 5", "Serving 5", "Address 5", "Deadline 5",
                "$15.99", "In Progress", null, null));
        testData.add(new Command(6L,"Title 6", "Dish 6", "Serving 6", "Address 6", "Deadline 6",
                "$18.99", "Prepared", null, null));
        return testData;
    }

    private void filterCommands(String status) {
        // Filter the list of commands based on the selected status
        List<Command> filteredCommands = new ArrayList<>();
        for (Command command : allCommands) {
            // Replace with your logic to determine the status of each command
            if (status.equals("ordered") && "Ordered".equals(command.getStatus())) {
                filteredCommands.add(command);
            } else if (status.equals("in_progress") && "In Progress".equals(command.getStatus())) {
                filteredCommands.add(command);
            } else if (status.equals("prepared") && "Prepared".equals(command.getStatus())) {
                filteredCommands.add(command);
            }
        }

        // Update the grid view with the filtered data
        updateGridView(filteredCommands);
    }

    private void updateGridView(List<Command> commands) {
        CommandAdapter adapter = new CommandAdapter(this, commands);
        gridViewDishes.setAdapter(adapter);
    }
}