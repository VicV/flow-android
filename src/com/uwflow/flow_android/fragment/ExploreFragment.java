package com.uwflow.flow_android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.uwflow.flow_android.R;
import com.uwflow.flow_android.adapters.SearchResultAdapter;
import com.uwflow.flow_android.db_object.Course;
import com.uwflow.flow_android.db_object.SearchResults;
import com.uwflow.flow_android.network.FlowApiRequestCallbackAdapter;
import com.uwflow.flow_android.network.FlowApiRequests;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wentaoji on 2014-02-18.
 */
public class ExploreFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "ExploreFragment";

    // NOTE: The positions here need to be in sync with
    private static final int SORT_POPULAR = 0;
    private static final int SORT_FRIENDS_TAKEN = 1;
    private static final int SORT_INTERESTING = 5;
    private static final int SORT_EASY = 2;
    private static final int SORT_HARD = 3;
    private static final int SORT_COURSE_CODE = 4;

    // TODO(david): Convert all the search stuff to use a SearchView, which gets us things like suggestions
    //     See http://developer.android.com/guide/topics/search/search-dialog.html
    private EditText mSearchBox;
    private Spinner mSortSpinner;
    private CheckBox mIncludeTakenCheckBox;
    private ListView mResultsListView;

    private List<Course> mSearchResultList;

    private SearchResultAdapter mSearchResultAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.explore_layout, container, false);
        mSearchBox = (EditText)rootView.findViewById(R.id.search_box);
        mSortSpinner = (Spinner)rootView.findViewById(R.id.sort_spinner);
        mIncludeTakenCheckBox = (CheckBox)rootView.findViewById(R.id.checkbox_include_taken);
        mResultsListView = (ListView)rootView.findViewById(R.id.results_list);

        mSearchResultList = new ArrayList<Course>();
        mSearchResultAdapter = new SearchResultAdapter(mSearchResultList, getActivity());
        mResultsListView.setAdapter(mSearchResultAdapter);
        mResultsListView.setOnItemClickListener(this);

        // Do search on sort mode change
        mSortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                doSearch();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e(TAG, "No sort mode selected. This should never happen.");
            }
        });

        // Do search on checkbox include/exclude courses taken change
        mIncludeTakenCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                doSearch();
            }
        });

        // Do search on search submit button click
        Button searchButton = (Button)rootView.findViewById(R.id.temp_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   doSearch();
            }
        });

        // Do search on edit text submit
        mSearchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    doSearch();
                    return true;
                }
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        // TODO(david): Add a "fetching results..." empty state here
        doSearch();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Course course = (Course)parent.getItemAtPosition(position);
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, CourseFragment.newInstance(course.getId()))
                .addToBackStack(null)
                .commit();
    }

    /**
     * Performs a search with parameters set on the UI.
     */
    private void doSearch() {
        // TODO(david): We should first gray-out results and show a spinner
        String searchString = mSearchBox.getText().toString();

        // TODO(david): Do search based on parameters

        FlowApiRequests.searchCourses(new FlowApiRequestCallbackAdapter() {
            @Override
            public void searchCoursesCallback(SearchResults searchResults) {
                // TODO(david): Is there a way to reset contents of a List?
                mSearchResultList.clear();
                mSearchResultList.addAll(searchResults.getCourses());
                mSearchResultAdapter.notifyDataSetChanged();
            }
        });

        // TODO(david): Load more search results button or pager or infinite scroll
    }
}
