package com.scoring.system.common;

import java.time.Instant;

public record BallWonEvent(Instant timestamp, String playerId) {}

