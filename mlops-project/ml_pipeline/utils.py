import logging
import os
import time
class Logger:
    """Logger class.

    Usage example:
        logger = Logger("ml-pipeline", debug=True).get()
    """

    def __init__(self, name: str, debug: bool = False):
        """Create and set up the logger.
        Args:
            name (str): Name of logger.
            debug (bool): Log debug messages in console.
        Returns:
            logging.Logger: Logger object.
        """
        self.logger = logging.getLogger(name)

        # set the minimum logging level
        self.logger.setLevel(logging.DEBUG)

        # set console logging level
        if debug:
            console_level = logging.DEBUG
        else:
            console_level = logging.INFO

        # set up console handler
        console_handler = logging.StreamHandler()
        console_handler.setLevel(console_level)
        # for console logs, just use level name and message
        console_handler.setFormatter(
            logging.Formatter("%(levelname)s: %(message)s")
        )
        self.logger.addHandler(console_handler)

        # set up file handler
        # The logs folder must be present
        # Check if the logs directory exists, and if not, create it
        logs_dir = "./logs"
        if not os.path.exists(logs_dir):
            os.makedirs(logs_dir)
        file_handler = logging.FileHandler("./logs/ml_pipeline.log")
        file_handler.setLevel(logging.DEBUG)
        # for file logs, provide more details
        formatter = logging.Formatter(
            "%(asctime)s:%(name)s:%(levelname)s: %(message)s"
        )
        # show time in UTC
        formatter.converter = time.gmtime
        file_handler.setFormatter(formatter)
        self.logger.addHandler(file_handler)

    def get(self) -> "logging.Logger":
        """Get a prepared logger object."""
        return self.logger