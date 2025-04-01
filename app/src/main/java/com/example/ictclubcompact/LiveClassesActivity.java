package com.example.ictclubcompact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LiveClassesActivity extends AppCompatActivity {

    private RecyclerView subjectsRecyclerView;
    private SearchView searchView;
    private SubjectsAdapter adapter;
    private final List<Subject> subjectList = new ArrayList<>();
    private final List<Subject> originalSubjectList = new ArrayList<>();
    ImageView backbtnlive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_classes);

        // Initialize views
        subjectsRecyclerView = findViewById(R.id.subjectsRecyclerView);
        searchView = findViewById(R.id.searchView);

        // Configure SearchView
        searchView.setIconifiedByDefault(true); // Start as icon only
        searchView.setQueryHint("Search subjects...");
        searchView.setOnClickListener(v -> {
            if (searchView.isIconified()) {
                searchView.setIconified(false); // Expand when clicked
            }
        });
        backbtnlive = findViewById(R.id.backbtnlive);
        backbtnlive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        // Setup subjects data
        setupSubjects();

        // Setup RecyclerView
        adapter = new SubjectsAdapter(subjectList);
        subjectsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        subjectsRecyclerView.setAdapter(adapter);

        // Setup search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void setupSubjects() {
        subjectList.clear();
        originalSubjectList.clear();

        // Add subjects with their live status
        subjectList.add(new Subject("Web Development", false, ""));
        subjectList.add(new Subject("Android App Development", true, "https://meet.google.com/xyz-android"));
        subjectList.add(new Subject("Flutter Development", false, ""));
        subjectList.add(new Subject("Python Programming", true, "https://meet.google.com/xyz-python"));
        subjectList.add(new Subject("Java Programming", false, ""));
        subjectList.add(new Subject("Microsoft Office", true, "https://meet.google.com/xyz-office"));

        originalSubjectList.addAll(subjectList);
    }

    // Adapter class
    class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder> implements Filterable {

        private List<Subject> filteredList;

        public SubjectsAdapter(List<Subject> subjects) {
            this.filteredList = new ArrayList<>(subjects);
        }

        // ViewHolder class
        class SubjectViewHolder extends RecyclerView.ViewHolder {
            TextView subjectName, liveStatus, liveBadge;
            Button joinButton;
            LinearLayout statusLayout;
            CardView cardView;

            SubjectViewHolder(View itemView) {
                super(itemView);
                subjectName = itemView.findViewById(R.id.subjectNameTextView);
                liveStatus = itemView.findViewById(R.id.liveStatusTextView);
                liveBadge = itemView.findViewById(R.id.liveBadgeTextView);
                joinButton = itemView.findViewById(R.id.joinButton);
                statusLayout = itemView.findViewById(R.id.liveStatusLayout);
                cardView = itemView.findViewById(R.id.cardView);
            }
        }

        @Override
        public SubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_subject, parent, false);
            return new SubjectViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SubjectViewHolder holder, int position) {
            Subject subject = filteredList.get(position);
            holder.subjectName.setText(subject.getName());

            if (subject.isLive()) {
                holder.liveStatus.setText("Live Now");
                holder.liveBadge.setVisibility(View.VISIBLE);
                holder.joinButton.setVisibility(View.VISIBLE);
                holder.statusLayout.setBackgroundColor(getResources().getColor(R.color.live_bg));

                holder.joinButton.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(subject.getLiveLink()));
                    startActivity(intent);
                });
            } else {
                holder.liveStatus.setText("No Live Yet");
                holder.liveBadge.setVisibility(View.GONE);
                holder.joinButton.setVisibility(View.GONE);
                holder.statusLayout.setBackgroundColor(getResources().getColor(R.color.not_live_bg));
            }
        }

        @Override
        public int getItemCount() {
            return filteredList.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    List<Subject> filteredResults = new ArrayList<>();
                    if (constraint == null || constraint.length() == 0) {
                        filteredResults.addAll(originalSubjectList);
                    } else {
                        String filterPattern = constraint.toString().toLowerCase().trim();
                        for (Subject item : originalSubjectList) {
                            if (item.getName().toLowerCase().contains(filterPattern)) {
                                filteredResults.add(item);
                            }
                        }
                    }
                    FilterResults results = new FilterResults();
                    results.values = filteredResults;
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredList.clear();
                    filteredList.addAll((List<Subject>) results.values);
                    notifyDataSetChanged();
                }
            };
        }
    }

    // Subject data class
    static class Subject {
        private final String name;
        private final boolean isLive;
        private final String liveLink;

        public Subject(String name, boolean isLive, String liveLink) {
            this.name = name;
            this.isLive = isLive;
            this.liveLink = liveLink;
        }

        public String getName() {
            return name;
        }

        public boolean isLive() {
            return isLive;
        }

        public String getLiveLink() {
            return liveLink;
        }
    }

}