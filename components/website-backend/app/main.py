""" Main module of the API."""

import logging
from contextlib import asynccontextmanager
from typing import Any, Dict

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.middleware.trustedhost import TrustedHostMiddleware

from app.api.utils.endpoints import base_router
from app.api.v1.api import api_v1_router
from app.core.config import settings
from app.core.middleware import ExceptionMonitorMiddleware
from app.core.utils.backend.alert_backend import alert_backend
from app.db.pre_start import pre_start
from app.dependencies import get_db
from app.schemas.base import HTTPError
from app.utils.get_version import get_version
from app.utils.logger import setup_logs

setup_logs("app", level=logging.DEBUG)
setup_logs("uvicorn.access")
setup_logs("sqlalchemy", level=logging.WARNING)

logging.getLogger("passlib").setLevel(logging.ERROR)


logger = logging.getLogger("app.main")


@asynccontextmanager
async def lifespan(_app: FastAPI):  # pragma: no cover
    """
    Context manager to run startup and shutdown events.
    """
    logger.info("Initializing database connection...")
    get_db.setup()
    await pre_start()
    logger.info("Database connection established.")
    yield
    logger.info("Closing database connection...")
    await get_db.shutdown()
    logger.info("Database connection closed.")


responses: Dict[int | str, Dict[str, Any]] | None = None


responses = {
    400: {"description": "Bad request", "model": HTTPError},
    401: {"description": "Unauthorized", "model": HTTPError},
    404: {"description": "Not found", "model": HTTPError},
    500: {
        "description": "Internal server error",
        "content": {"text/plain": {"example": "Internal server error"}},
    },
}


app = FastAPI(
    title="Website API",
    openapi_url=f"{settings.API_V1_PREFIX}/openapi.json",
    version=get_version(),
    lifespan=lifespan,
    responses=responses,
)

app.add_middleware(ExceptionMonitorMiddleware, alert_backend=alert_backend())
app.add_middleware(TrustedHostMiddleware, allowed_hosts=settings.ALLOWED_HOSTS)
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.ALLOWED_HOSTS,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


app.include_router(base_router, prefix=settings.API_V1_PREFIX)
app.include_router(api_v1_router, prefix=settings.API_V1_PREFIX)