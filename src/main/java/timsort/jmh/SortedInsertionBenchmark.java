/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package timsort.jmh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class SortedInsertionBenchmark {

	private static final int LIST_SIZE = 100_000;

	private final List<Integer> original = new ArrayList<>(LIST_SIZE);
	private final Random rndAppendSort, rndSearchInsert;

	public SortedInsertionBenchmark() {
		final Random rnd = new Random();
		for (int i = 0; i < LIST_SIZE; i++) {
			original.add(rnd.nextInt());
		}
		Collections.sort(original);

		final int commonSeed = rnd.nextInt();
		rndAppendSort = new Random(commonSeed);
		rndSearchInsert = new Random(commonSeed);
	}

	@Benchmark
	public void appendSort() {
		final int newEntry = rndAppendSort.nextInt();
		List<Integer> copy = new ArrayList<>(original);
		copy.add(newEntry);
		Collections.sort(copy);

		if (!isSorted(copy)) {
			throw new IllegalStateException(String.format("List %s not sorted after adding %d", copy, newEntry));
		}
	}

	@Benchmark
	public void searchInsert() {
		final int newEntry = rndSearchInsert.nextInt();
		List<Integer> copy = new ArrayList<>(original);
		final int insertPosition = findSortedInsertionPoint(newEntry, copy);
		copy.add(insertPosition, newEntry);

		if (!isSorted(copy)) {
			throw new IllegalStateException(String.format("List %s not sorted after adding %d", copy, newEntry));
		}
	}

	private static int findSortedInsertionPoint(final int newEntry, List<Integer> copy) {
		int low = 0, high = copy.size();
		while (low < high) {
			final int midPosition = (low + high) / 2;
			final int midValue = copy.get(midPosition);

			if (midPosition == low) {
				// Interval (low, high) only covers two consecutive
				// positions
				if (newEntry <= midValue) {
					return low;
				} else {
					return high;
				}
			} else if (newEntry < midValue) {
				high = midPosition;
			} else if (newEntry > midValue) {
				low = midPosition;
			} else {
				return midPosition;
			}
		}
		return low;
	}

	private static boolean isSorted(List<Integer> l) {
		Iterator<Integer> it = l.iterator();

		int prev = it.next();
		while (it.hasNext()) {
			final int curr = it.next();
			if (curr < prev) {
				return false;
			}
			prev = curr;
		}
		return true;
	}

}
