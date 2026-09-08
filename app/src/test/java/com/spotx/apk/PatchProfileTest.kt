package com.spotx.apk

import com.spotx.apk.data.PatchId
import com.spotx.apk.data.PatchProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PatchProfileTest {
    @Test
    fun full_profile_contains_all_patches() {
        assertEquals(PatchId.entries.toSet(), PatchProfile.FULL.patches)
    }

    @Test
    fun minimal_profile_is_subset_of_full() {
        assertTrue(PatchProfile.FULL.patches.containsAll(PatchProfile.MINIMAL.patches))
    }
}
