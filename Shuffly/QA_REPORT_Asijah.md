# Shuffly — QA Testing Report
**Team 19 | CSC 3380**  
**QA Tester:** Asijah Wright  
**Date:** November 22, 2026

## 1. Tests Passed
- Application compiles and launches successfully.
- Spotify authentication and account connection work after login.
- Library displays 6 Liked Songs.
- Search finds songs by artist and title.
- Partial searches return matching songs.
- Search is case-insensitive.
- Nonexistent searches display "No songs found."
- Clearing the search bar displays the empty-search prompt.
- All four shuffle modes can be selected.
- Shuffle mode selection persists across page navigation.
- Recently Played history was retained during navigation.
- Clicking Next Track with an empty queue does not crash the application.
- Clicking Previous Track with an empty queue does not crash the application.

## 2. Bug: Now Playing Synchronization

**Steps to reproduce:**
1. Connect Shuffly to Spotify.
2. Play or change a song in Spotify.
3. Open Shuffly's Queue page.
4. Compare Queue → Now Playing with the bottom player.

**Expected:** Both sections display the same current song.

**Actual:** Spotify and Shuffly's bottom player displayed "Strange" by Jhené Aiko, while Queue → Now Playing displayed "Keep Doing What You're Doing" by Bryson Tiller.

**Status:** Observed repeatedly. Needs investigation, including whether expired authentication contributes to stale queue data.

## 3. Authentication Issue

**Actual:** Spotify API requests repeatedly returned HTTP 401 (missing, invalid, or expired access token).

**Expected:** An expired token should be refreshed and the request retried when possible.

**Status:** Fenista is working on automatic token refresh in SpotifyApiClient.

## 4. Artist Spacing — Needs Verification

With Artist Spacing selected, two SZA songs appeared consecutively under Up Next.

However, it has not been confirmed whether switching modes regenerated the queue. This is not yet a confirmed algorithm bug.

## 5. Testing Limitations

The Spotify account uses Spotify Free. Playback requests returned HTTP 403 with "Premium required."

Full playback control and actual shuffle algorithm behavior remain unverified.

## 6. Remaining QA Work

- Verify all four shuffle algorithms using a newly generated queue.
- Retest Queue synchronization after authentication fixes.
- Test empty, single-song, and duplicate-heavy playlists.
- Retest playback controls with a supported Spotify Premium account, if available.
- Record final results and share them with the development team.