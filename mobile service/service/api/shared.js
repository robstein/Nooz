exports.rankingAlgos = {
    "RelevanceMinusIrrelevance": function (a, b) {
        if (a.relevantScore - a.irrelevantScore < b.relevantScore - b.irrelevantScore)
            return 1;
        if (a.relevantScore - a.irrelevantScore > b.relevantScore - b.irrelevantScore)
            return -1;
        return 0;
    }
};