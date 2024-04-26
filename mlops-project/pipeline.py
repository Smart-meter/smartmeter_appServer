import argparse
import pathlib
import sys
import time

from ml_pipeline import config, dataset_factory, model_factory, utils

def pipeline_task(task_func):
    """Decorator that adds a boolean attribute to the given function,
        indicating that it is a pipeline task."""
    task_func.is_task = True
    return task_func
class MLPipeline:
    """ML training pipeline.

        Encapsulates all pipeline-related functionality.

        Attributes:
            logger (logging.Logger): Logger object.
            config (config): Pipeline configuration.
            tasks (List[Callable]): Pipeline tasks.
            timestamp (int): Unix time of run.
            artifact_dir (str): Path to artifact directory.
            dataset (dataset): Dataset object.
            model (model): Model object.
            idx_train (pd.Int64Index): Indices of training examples.
            idx_test (pd.Int64Index): Indices of test examples.
        """
    def __init__(
            self:"MLPipeline", project_config_path: str, logger: "logging.Logger")->None:
        """
        Initialises the pipeline.
        :param project_config_path:
        :param logger: Logger object
        """
        # load config file and get tasks
        project_config_path = pathlib.Path(project_config_path)
        logger.debug(project_config_path.parent)
        logger.debug(project_config_path.stem)
        self.config = config.Config(
            project_config_path.parent, project_config_path.stem
        )
        self.config.load()
        self.tasks = []
        for task in self.tasks:
            try:
                func = getattr(self, task)
            except AttributeError:
                raise Exception(f"'{task}' is not defined in the pipeline.")

            if hasattr(func, "is_task"):
                self.tasks.append(func)
            else:
                raise Exception(f"'{func}' is not a pipeline task.")

    @pipeline_task
    def load_data(self) -> None:
        # ...
        pass

    @pipeline_task
    def preprocess_data(self) -> None:
        # ...
        pass

    def not_a_task(self, a, b) -> None:
        # ...
        pass

    def run(self) -> None:
        for task in self.tasks:
            task()
if __name__ == "__main__":
    #Create the parser object, initializing it with the description of the program
    parser = argparse.ArgumentParser(
        description="Machine learning training pipeline.", allow_abbrev=False
    )
    #Add each of the command-line arguments to the parser
    parser.add_argument(
        "-c",
        "--config",
        type=str,
        help="path to project configuration file",
        required=True,
    )
    parser.add_argument(
        "-d", "--debug", action="store_true", help="run in debug mode"
    )
    #Parse the input arguments
    args = parser.parse_args()
    logger = utils.Logger("ml-pipeline", debug=args.debug).get()
    logger.debug(args.config)

    try:
        pipeline = MLPipeline(args.config, logger)
        pipeline.run()
    except Exception as error:
        logger.error(error)
        sys.exit(-1)

    logger.info("Pipeline run complete")