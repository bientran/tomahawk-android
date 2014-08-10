/* == This file is part of Tomahawk Player - <http://tomahawk-player.org> ===
 *
 *   Copyright 2012, Enno Gottschalk <mrmaffen@googlemail.com>
 *
 *   Tomahawk is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Tomahawk is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Tomahawk. If not, see <http://www.gnu.org/licenses/>.
 */
package org.tomahawk.tomahawk_android.fragments;

import org.tomahawk.libtomahawk.collection.Artist;
import org.tomahawk.libtomahawk.database.DatabaseHelper;
import org.tomahawk.libtomahawk.infosystem.InfoSystem;
import org.tomahawk.tomahawk_android.adapters.TomahawkListAdapter;
import org.tomahawk.tomahawk_android.utils.FragmentUtils;
import org.tomahawk.tomahawk_android.utils.TomahawkListItem;

import android.content.Context;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link TomahawkFragment} which shows a set of {@link Artist}s inside its {@link
 * se.emilsjolander.stickylistheaders.StickyListHeadersListView}
 */
public class ArtistsFragment extends TomahawkFragment {

    public static final int SHOW_MODE_STARREDARTISTS = 1;

    @Override
    public void onResume() {
        super.onResume();

        if (getArguments() != null) {
            if (getArguments().containsKey(SHOW_MODE)) {
                mShowMode = getArguments().getInt(SHOW_MODE);
            }
        }
        updateAdapter();
    }

    /**
     * Called every time an item inside a ListView or GridView is clicked
     *
     * @param item the TomahawkListItem which corresponds to the click
     */
    @Override
    public void onItemClick(TomahawkListItem item) {
        if (item instanceof Artist) {
            FragmentUtils.replace(getActivity(), getActivity().getSupportFragmentManager(),
                    AlbumsFragment.class, item.getCacheKey(),
                    TomahawkFragment.TOMAHAWK_ARTIST_KEY, mCollection);
        }
    }

    /**
     * Update this {@link TomahawkFragment}'s {@link TomahawkListAdapter} content
     */
    @Override
    protected void updateAdapter() {
        if (!mIsResumed) {
            return;
        }

        Context context = getActivity();
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        List<TomahawkListItem> artists = new ArrayList<TomahawkListItem>();
        if (mShowMode == SHOW_MODE_STARREDARTISTS) {
            ArrayList<Artist> starredArtists = DatabaseHelper.getInstance().getStarredArtists();
            for (Artist artist : starredArtists) {
                ArrayList<String> requestIds = InfoSystem.getInstance().resolve(artist, false);
                for (String requestId : requestIds) {
                    mCurrentRequestIds.add(requestId);
                }
            }
            artists.addAll(starredArtists);
            if (getListAdapter() == null) {
                TomahawkListAdapter tomahawkListAdapter = new TomahawkListAdapter(context,
                        layoutInflater, artists, this);
                setListAdapter(tomahawkListAdapter);
            } else {
                ((TomahawkListAdapter) getListAdapter()).setListItems(artists);
            }
        } else {
            artists.addAll(mCollection.getArtists());
            if (getListAdapter() == null) {
                TomahawkListAdapter tomahawkListAdapter = new TomahawkListAdapter(context,
                        layoutInflater, artists, this);
                tomahawkListAdapter.setShowArtistAsSingleLine(mCollection != null);
                setListAdapter(tomahawkListAdapter);
            } else {
                ((TomahawkListAdapter) getListAdapter()).setListItems(artists);
            }
        }
    }
}
