/*
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */

package io.crate.planner.node.dql;

import io.crate.planner.Plan;
import io.crate.planner.PlanAndPlannedAnalyzedRelation;
import io.crate.planner.PlanVisitor;
import io.crate.planner.distribution.UpstreamPhase;
import io.crate.planner.node.fetch.FetchPhase;
import io.crate.planner.projection.Projection;

import javax.annotation.Nullable;
import java.util.UUID;

public class QueryThenFetch extends PlanAndPlannedAnalyzedRelation {

    private final FetchPhase fetchPhase;
    private final Plan subPlan;
    private final MergePhase localMerge;
    private final UUID id;

    public QueryThenFetch(Plan subPlan, FetchPhase fetchPhase, @Nullable MergePhase localMerge, UUID id) {
        this.subPlan = subPlan;
        this.fetchPhase = fetchPhase;
        this.localMerge = localMerge;
        this.id = id;
    }

    public FetchPhase fetchPhase() {
        return fetchPhase;
    }

    @Nullable
    public MergePhase localMerge() {
        return localMerge;
    }

    public Plan subPlan() {
        return subPlan;
    }

    @Override
    public <C, R> R accept(PlanVisitor<C, R> visitor, C context) {
        return visitor.visitQueryThenFetch(this, context);
    }

    @Override
    public UUID jobId() {
        return id;
    }

    @Override
    public void addProjection(Projection projection) {
        throw new UnsupportedOperationException("Adding projections to QTF is not possible");
    }

    @Override
    public boolean resultIsDistributed() {
        return false;
    }

    @Override
    public UpstreamPhase resultPhase() {
        assert localMerge != null;
        return localMerge;
    }
}
