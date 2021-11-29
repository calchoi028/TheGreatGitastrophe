package com.example.cmput_301_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FriendProfilePage extends AppCompatActivity {

    // TODO: random values chosen need to be replaced w real complete/incomplete habits
    private String friendName = "";
    /* TODO: isFollowing should be a real value corresponding to if user is
        following friend's habits, currently set to false to allow follow button to work
     */
    private RecyclerView recyclerView;
    private ProfileHabitAdapter profileHabitAdapter;
    AccountData accountData;
    TextView progressText;
    ProgressBar progressBar;
    private int progress = 0;
    int progressMaxCounter = 0, progressCurrentCounter = 0;
    int[] progressRate = new int[2];
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile_page);

        // TODO: add code to change friend profile photo here

        TextView friendNameView = (TextView) findViewById(R.id.friendUsernameTV);

        recyclerView = (RecyclerView) findViewById(R.id.habitList);
        progressBar = findViewById(R.id.progress_bar);
        progressText = findViewById(R.id.prg_value);

        extras = getIntent().getExtras();
        accountData = AccountData.create();
        Account friendAccount = accountData.getAccountData().get(extras.getString("friendId"));
        friendName = friendAccount.getUserName();
        friendNameView.setText(friendName);

        // Percent completed habits per month graphic update
        for (Habit habit : friendAccount.getHabitTable()) {
            progressRate = friendAccount.getHabitCompletionRateInLastThirtyDays(habit.getId());
            progressCurrentCounter += progressRate[1];
            progressMaxCounter += progressRate[0];
        }
        if (progressMaxCounter != 0) {
            progress = 100 * progressCurrentCounter / progressMaxCounter;
        } else {
            progress = 0;
        }
        // Progress bar library bug fix (not our fault)
        if (progress < 100) {
            updateProgress(progress + 1);
        } else {
            updateProgress(progress - 1);
        }
        updateProgress(progress);

        ArrayList<HabitEvent> eventList = new ArrayList<HabitEvent>();
        friendAccount.getRecentHabitEvents(eventList, false);
        profileHabitAdapter = new ProfileHabitAdapter(eventList,this);
        recyclerView.setAdapter(profileHabitAdapter);
    }

    /**
     * This function is called when the 'Detailed Habit List' button is pressed,
     * taking user to FriendDetailedHabitListPage
     * @param view current view containing habitListButton
     */
    public void onDetailedHabitsClick(View view) {
        Intent switchToHabitsPage = new Intent(this, FriendHabits.class);
        switchToHabitsPage.putExtra("userId", extras.getString("friendId"));
        startActivity(switchToHabitsPage);
    }

    /**
     * Method to set the progress value i.e % of progress
     * bar filled out */
    public void updateProgress(int progress)
    {
        progressBar.setProgress(progress);
        progressText.setText(progress + "%");
    }
}